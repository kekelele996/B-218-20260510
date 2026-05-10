# 领养申请全流程分析

## 一、整体流程图

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    用户操作层                                            │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  ① 宠物详情页           ② 申请表填写页           ③ 申请列表页           ④ 申请详情页     │
│  ┌──────────┐         ┌──────────────┐         ┌──────────────┐       ┌──────────────┐  │
│  │ PetDetail │────────▶│    Apply     │────────▶│ Applications │──────▶│Application   │  │
│  │ 点击申请  │  路由跳转 │  填表+提交   │ 提交成功 │  查看记录    │ 点击  │  Detail      │  │
│  └──────────┘         └──────────────┘         └──────────────┘       └──────────────┘  │
│       │                     │                      │                        │            │
└───────┼─────────────────────┼──────────────────────┼────────────────────────┼────────────┘
        │                     │                      │                        │
        ▼                     ▼                      ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    前端 API 层                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  petApi.getDetail()   adoptionApi.apply()   adoptionApi.getList()  adoptionApi.getDetail│
│  GET /api/pet/{id}    POST /api/adoption/   GET /api/adoption/     GET /api/adoption/   │
│                        apply                list                    {id}                  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
        │                     │                      │                        │
        ▼                     ▼                      ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    后端 Controller 层                                   │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  PetController         AdoptionController          AdoptionController                    │
│  .getDetail()          .apply()                    .getMyApplications()  .getDetail()    │
│                        ↓ 校验 → 创建 → 入库        ↓ 查询+关联宠物       ↓ 查询+权限校验│
└─────────────────────────────────────────────────────────────────────────────────────────┘
        │                     │                      │                        │
        ▼                     ▼                      ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    后端 Service 层                                      │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  PetServiceImpl        AdoptionServiceImpl         AdoptionServiceImpl                   │
│  .getDetail()          .apply()                    .getMyApplications()  .getDetail()    │
│  查询宠物信息          宠物校验+重复检查+           分页查询+宠物信息      权限校验+宠物  │
│                        字段校验+创建记录            组装VO                信息组装        │
└─────────────────────────────────────────────────────────────────────────────────────────┘
        │                     │                      │                        │
        ▼                     ▼                      ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 MyBatis Mapper 层                                      │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  PetMapper             AdoptionMapper               AdoptionMapper                       │
│  .selectById()         .insert()                    .selectByUserId()     .selectById()  │
│                                                    .selectCountByUserId()                │
└─────────────────────────────────────────────────────────────────────────────────────────┘
        │                     │                      │                        │
        ▼                     ▼                      ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 MySQL 数据库层                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  pet 表                adoption_application 表     adoption_application 表               │
│  SELECT ... WHERE id=  INSERT INTO ...              SELECT ... WHERE                    │
│                        status=0(待审核)             user_id= + ORDER BY                 │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、状态流转图

```
                    ┌──────────┐
                    │  用户提交  │
                    └────┬─────┘
                         │
                         ▼
                  ┌──────────────┐
           ┌──────│  0 待审核     │──────┐
           │      └──────────────┘      │
           │           │                │
      用户取消    管理员受理         管理员受理
           │           │                │
           ▼           ▼                ▼
    ┌──────────────┐  ┌──────────────┐
    │  4 已取消     │  │  1 审核中     │
    └──────────────┘  └──────┬───────┘
                       ┌─────┴──────┐
                       │            │
                  管理员通过    管理员拒绝
                       │            │
                       ▼            ▼
                ┌──────────────┐ ┌──────────────┐
                │  2 已通过     │ │  3 已拒绝     │
                └──────────────┘ └──────────────┘
                       │
                 宠物状态变为
                 status=2(已领养)
```

**状态值对照表（`adoption_application.status`）**：

| 值 | 状态 | 含义 | 可执行操作 |
|----|------|------|-----------|
| 0  | 待审核 | 刚提交，等待管理员受理 | 用户可取消 |
| 1  | 审核中 | 管理员已受理，正在审核 | — |
| 2  | 已通过 | 审核通过，宠物完成领养 | 可预约面谈 |
| 3  | 已拒绝 | 审核未通过 | — |
| 4  | 已取消 | 用户主动取消 | 不可恢复 |

---

## 三、分步详细说明

### 步骤 1：用户在详情页点击"申请领养"

**前端（PetDetail.vue）**

- 页面加载时调用 `petApi.getDetail(route.params.id)` → `GET /api/pet/{id}` 获取宠物详情
- 根据宠物状态 `pet.status` 决定按钮显示：
  - `status === 1`（可领养）→ 显示"申请领养"按钮
  - `status === 2`（已领养）→ 显示"已被领养"
  - 其他 → 显示"暂不可领养"
- 已登录用户：点击按钮跳转 `/apply/${pet.id}`（Apply.vue）
- 未登录用户：点击按钮跳转登录页，并携带 `redirect=/apply/${pet.id}`，登录后自动回跳

**路由守卫（router/index.js）**

- Apply 路由配置了 `meta: { requiresAuth: true }`
- `beforeEach` 守卫检查 `userStore.isLoggedIn`，未登录则重定向到 Login 页

**后端**

- `PetController` → `PetServiceImpl.getDetail()` → `PetMapper.selectById()` 查询 pet 表
- 返回 `PetDetailVO`（含 name、breed、images、status 等字段）

---

### 步骤 2：用户填写申请表并提交

**前端（Apply.vue）**

- 页面加载时调用 `petApi.getDetail(petId)` 获取宠物信息，展示在表单顶部卡片
- 表单字段及校验：

| 字段 | 组件类型 | 校验规则 |
|------|---------|---------|
| livingType（居住类型） | 按钮组（独居/家人同住/合租） | 必填 |
| housingType（住房情况） | 按钮组（自有房产/租房） | 必填 |
| hasYard（是否有院子） | 按钮组（是/否） | 必填 |
| experience（养宠经验） | 下拉选择（无/1-3年/3年以上） | 必填 |
| dailyTime（每日陪伴时间） | 下拉选择（<2h/2-5h/>5h） | 必填 |
| currentPets（现有宠物） | 多选按钮（猫/狗/其他/无） | 非必填 |
| reason（领养理由） | 文本域 | 50~500字 |
| contactAddress（联系地址） | 文本输入 | ≥5字符 |

- 前端计算属性 `isFormValid` 实时校验，按钮在表单不合法时 disabled
- 点击"提交申请"弹出确认弹窗（Modal），确认后调用 `handleSubmit()`
- 提交时将 `hasYard` 从布尔值转为 `1/0` 整数，`currentPets` 保持数组
- 调用 `adoptionApi.apply(data)` → `POST /api/adoption/apply`

**后端请求链路**：

1. **LoginInterceptor**：从 `Authorization: Bearer <token>` 提取 JWT，解析出 userId，存入 `UserContext`（ThreadLocal）
2. **AdoptionController.apply()**：从 `UserContext.getUserId()` 获取当前用户 ID
3. **AdoptionServiceImpl.apply()**（@Transactional 事务）：
   - **校验宠物**：查询 pet 表确认宠物存在且 `status=1`（可领养）
   - **重复申请检查**：`AdoptionMapper.existsByUserIdAndPetId()` 查询是否存在 status IN (0,1,2) 的记录
   - **字段校验**：逐项检查必填字段（livingType、housingType、hasYard、experience、dailyTime、reason、contactAddress）
   - **创建实体**：构造 `AdoptionApplication`，设置 `status=0`（待审核）
   - **处理 currentPets**：将 List 用逗号拼接为字符串存储
   - **入库**：`AdoptionMapper.insert()` → `INSERT INTO adoption_application`

**数据库变更**：

```sql
INSERT INTO adoption_application
  (user_id, pet_id, living_type, housing_type, has_yard, experience,
   current_pets, daily_time, reason, contact_address, status, create_time, update_time)
VALUES
  (#{userId}, #{petId}, #{livingType}, #{housingType}, #{hasYard}, #{experience},
   #{currentPets}, #{dailyTime}, #{reason}, #{contactAddress}, 0, NOW(), NOW());
```

- 插入后通过 `useGeneratedKeys=true` 回填自增主键 id 到 application 对象

**提交成功响应**：

```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": { "applicationId": 123 }
}
```

- 前端收到成功后：toast 提示 + `router.push('/user/applications')` 跳转到申请列表

---

### 步骤 3：用户查看申请列表

**前端（Applications.vue）**

- 页面加载时调用 `adoptionApi.getList(params)` → `GET /api/adoption/list`
- 支持状态筛选：全部 / 待审核(0) / 审核中(1) / 已通过(2) / 已拒绝(3) / 已取消(4)
- 分页：默认 pageSize=10，支持翻页
- 切换状态时重置页码并重新加载

**后端请求链路**：

1. **LoginInterceptor**：JWT 解析获取 userId
2. **AdoptionController.getMyApplications()**：接收 page、pageSize、status 参数
3. **AdoptionServiceImpl.getMyApplications()**：
   - 计算 offset = (page - 1) * pageSize
   - 调用 `AdoptionMapper.selectByUserId(userId, status, offset, pageSize)` 查询列表
   - 调用 `AdoptionMapper.selectCountByUserId(userId, status)` 查询总数
   - **关联宠物信息**：对每条申请，调用 `PetMapper.selectById(petId)` 获取宠物名称、品种、图片
   - 组装 `AdoptionVO`（id、status、statusText、createTime、pet 信息）
   - 返回 `PageResult<AdoptionVO>`

**数据库查询**：

```sql
-- 查询列表（按创建时间倒序）
SELECT * FROM adoption_application
WHERE user_id = #{userId}
  AND status = #{status}   -- 可选筛选
ORDER BY create_time DESC
LIMIT #{offset}, #{pageSize};

-- 查询总数
SELECT COUNT(1) FROM adoption_application
WHERE user_id = #{userId}
  AND status = #{status};   -- 可选筛选

-- 逐条关联查询宠物信息
SELECT * FROM pet WHERE id = #{petId};
```

**列表页展示逻辑**：

- 每条记录展示：宠物图片、宠物名、品种、提交日期、状态标签
- 状态标签颜色：待审核=黄色、审核中=蓝色、已通过=绿色、已拒绝=红色、已取消=灰色
- 已拒绝(3)和已取消(4)的记录，宠物图片和名称半透明显示
- 操作按钮：
  - 所有记录可点击"查看详情"跳转 `/user/application/{id}`
  - 待审核(0)状态显示"取消申请"按钮
  - 已通过(2)状态显示"预约面谈"按钮

> **⚠️ 前后端数据结构不匹配 Bug**：后端 `AdoptionVO` 返回的宠物信息是嵌套在 `pet` 对象中（`pet.name`、`pet.breed`、`pet.image`），但前端模板使用的是扁平字段 `app.petName`、`app.petBreed`、`app.petImages`，后端并不返回这些字段，导致宠物名称、品种、图片均无法展示。正确写法应为 `app.pet.name`、`app.pet.breed`、`app.pet.image`。详见第七节。

---

### 步骤 4：用户查看申请详情

**前端（ApplicationDetail.vue）**

- 调用 `adoptionApi.getDetail(route.params.id)` → `GET /api/adoption/{id}`

**后端请求链路**：

1. **LoginInterceptor**：JWT 解析获取 userId
2. **AdoptionController.getApplicationDetail()**
3. **AdoptionServiceImpl.getApplicationDetail()**：
   - 查询申请记录，不存在则抛 404
   - **权限校验**：检查 `application.userId == userId`，不匹配则抛 403
   - 组装 `AdoptionDetailVO`：含所有申请字段 + 宠物信息 + 审核备注
   - **解析 currentPets**：将逗号分隔字符串拆分为 List
   - **解析 hasYard**：将整数 1/0 转为 Boolean

**数据库查询**：

```sql
SELECT * FROM adoption_application WHERE id = #{id};
SELECT * FROM pet WHERE id = #{petId};
```

**详情页展示**：
- 宠物信息卡片（图片、名称、品种、状态标签）
- 申请信息（申请时间、居住类型、住房情况、院子、养宠经验、陪伴时间、现有宠物、地址、理由）
- 审核备注（remark 字段，有值时显示）
- 操作：待审核状态可取消申请

---

### 步骤 5（补充）：用户取消申请

**前端**
- 列表页或详情页点击"取消申请"→ 确认弹窗 → `adoptionApi.cancel(id)` → `PUT /api/adoption/{id}/cancel`

**后端（AdoptionServiceImpl.cancelApplication()）**

- @Transactional 事务
- 查询申请记录，不存在抛 404
- 权限校验：userId 不匹配抛 403
- **状态校验**：只有 `status=0`（待审核）可取消，其他状态抛 400
- 调用 `AdoptionMapper.updateStatus(id, 4)` → `UPDATE adoption_application SET status=4, update_time=NOW() WHERE id=#{id}`

---

## 四、前后端数据格式对照

### 提交申请请求体（AdoptionApplyDTO）

```json
{
  "petId": 1,
  "livingType": "家人同住",
  "housingType": "自有房产",
  "hasYard": true,
  "experience": "1-3年",
  "dailyTime": "2-5小时",
  "currentPets": ["猫", "狗"],
  "reason": "我一直很喜欢小动物...(50-500字)",
  "contactAddress": "北京市朝阳区xxx"
}
```

### 申请列表返回（AdoptionVO）

```json
{
  "id": 123,
  "pet": {
    "id": 1,
    "name": "大黄",
    "breed": "金毛",
    "type": "狗",
    "age": 24,
    "gender": "公",
    "image": "https://xxx.jpg"
  },
  "status": 0,
  "statusText": "待审核",
  "createTime": "2026-05-10T14:30:00"
}
```

### 申请详情返回（AdoptionDetailVO）

```json
{
  "id": 123,
  "pet": { "..." : "同上 PetSimpleVO" },
  "livingType": "家人同住",
  "housingType": "自有房产",
  "hasYard": true,
  "experience": "1-3年",
  "dailyTime": "2-5小时",
  "currentPets": ["猫", "狗"],
  "reason": "我一直很喜欢小动物...",
  "contactAddress": "北京市朝阳区xxx",
  "status": 0,
  "statusText": "待审核",
  "remark": null,
  "createTime": "2026-05-10T14:30:00",
  "updateTime": "2026-05-10T14:30:00"
}
```

---

## 五、认证鉴权机制

整个流程中，所有 `/api/adoption/**` 接口都经过 `LoginInterceptor` 拦截：

```
请求 → LoginInterceptor.preHandle()
         │
         ├─ 提取 Authorization Header → Bearer <JWT>
         ├─ JwtUtil.parseToken() 解析 token
         │   ├─ 成功 → userId 存入 UserContext(ThreadLocal) → 放行
         │   ├─ ExpiredJwtException → 返回 401 "登录已过期"
         │   └─ 其他异常 → 返回 401 "Token无效"
         │
         └─ 无 Header → 返回 401 "未登录"

请求完成 → LoginInterceptor.afterCompletion()
         └─ UserContext.clear() 清除 ThreadLocal，防止内存泄漏
```

前端通过 `userStore.isLoggedIn` 判断登录状态（基于 localStorage 中的 token），路由守卫在未登录时重定向到登录页。

---

## 六、关键代码位置索引

| 环节 | 文件 | 关键行 |
|------|------|--------|
| 详情页申请按钮 | `frontend/src/views/PetDetail.vue` | 第 110~125 行 |
| 申请表填写与提交 | `frontend/src/views/Apply.vue` | 第 270~299 行（handleSubmit） |
| 前端 API 封装 | `frontend/src/api/adoption.js` | 全文 |
| 路由守卫 | `frontend/src/router/index.js` | 第 82~102 行 |
| 后端申请接口 | `backend/.../controller/AdoptionController.java` | 第 27~34 行 |
| 后端申请业务逻辑 | `backend/.../service/impl/AdoptionServiceImpl.java` | 第 31~90 行（apply） |
| 重复申请检查 | `backend/.../mapper/AdoptionMapper.xml` | 第 57~59 行（existsByUserIdAndPetId） |
| 列表查询 | `backend/.../service/impl/AdoptionServiceImpl.java` | 第 93~129 行 |
| 详情查询+权限校验 | `backend/.../service/impl/AdoptionServiceImpl.java` | 第 132~182 行 |
| 取消申请 | `backend/.../service/impl/AdoptionServiceImpl.java` | 第 186~203 行 |
| JWT 认证拦截器 | `backend/.../interceptor/LoginInterceptor.java` | 第 22~49 行 |
| UserContext(ThreadLocal) | `backend/.../util/UserContext.java` | 全文 |
| 数据库表定义 | `backend/src/main/resources/sql/schema.sql` | 第 88~111 行 |
| MyBatis SQL 映射 | `backend/src/main/resources/mapper/AdoptionMapper.xml` | 全文 |

---

## 七、已知问题：申请列表前后端数据结构不匹配

### 问题描述

申请列表页（Applications.vue）中，前端模板引用的宠物字段名与后端实际返回的数据结构不一致，导致宠物名称、品种、图片均无法正常展示。

### 后端实际返回结构

`AdoptionVO` 中宠物信息封装在嵌套的 `pet` 对象（类型 `PetSimpleVO`）中：

```
AdoptionVO
├── id: Long
├── status: Integer
├── statusText: String
├── createTime: LocalDateTime
└── pet: PetSimpleVO          ← 嵌套对象
    ├── id: Long
    ├── name: String           ← 宠物名称
    ├── breed: String          ← 品种
    ├── type: String
    ├── age: Integer
    ├── gender: String
    └── image: String          ← 宠物图片（单张）
```

对应 JSON：

```json
{
  "id": 123,
  "pet": {
    "id": 1,
    "name": "大黄",
    "breed": "金毛",
    "type": "狗",
    "age": 24,
    "gender": "公",
    "image": "https://xxx.jpg"
  },
  "status": 0,
  "statusText": "待审核",
  "createTime": "2026-05-10T14:30:00"
}
```

### 前端实际引用方式

Applications.vue 模板中使用了不存在的扁平字段：

| 模板引用 | 所在行 | 后端实际字段 | 结果 |
|---------|--------|------------|------|
| `app.petName` | 第 49 行 | `app.pet.name` | 显示为空，回退到"宠物" |
| `app.petBreed` | 第 59 行 | `app.pet.breed` | 显示为空，回退到"未知品种" |
| `app.petImages`（getAppImage 函数第 159 行） | 第 159 行 | `app.pet.image` | 图片无法加载，回退到默认图 |

### 修复方案

前端模板应改为通过 `pet` 嵌套对象访问：

| 当前写法（错误） | 正确写法 |
|----------------|---------|
| `app.petName` | `app.pet?.name` |
| `app.petBreed` | `app.pet?.breed` |
| `app.petImages`（getAppImage 函数） | `app.pet?.image` |

涉及修改的文件位置：

- `frontend/src/views/user/Applications.vue` 第 49 行：`app.petName` → `app.pet?.name`
- `frontend/src/views/user/Applications.vue` 第 59 行：`app.petBreed` → `app.pet?.breed`
- `frontend/src/views/user/Applications.vue` 第 158~162 行（`getAppImage` 函数）：`app.petImages` → `app.pet?.image`

### 影响范围

- **申请列表页**（Applications.vue）：宠物名称、品种、图片均无法展示，用户只能看到"宠物"和"未知品种"的回退文案
- **申请详情页**（ApplicationDetail.vue）：同样使用 `application.petName` 和 `application.petBreed`（第 31~32 行），也存在相同问题，需同步修改为 `application.pet?.name` 和 `application.pet?.breed`
