# 领养申请全流程说明文档

## 一、流程图

```
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                      用户操作                                                       │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                                         ▼
                           ┌───────────────────────────┐
                           │     1. 宠物详情页           │
                           │   PetDetail.vue:108-131     │
                           │   检查 pet.status === 1     │
                           └─────────────┬───────────────┘
                                         │
                    ┌────────────────────┴────────────────────┐
                    │                    已登录?                │
                    └───┬──────────────────────────────────┬───┘
                        │ 否                              │ 是
                        ▼                                  ▼
           ┌─────────────────────┐             ┌─────────────────────────┐
           │  跳转登录页           │             │  跳转 /apply/{petId}    │
           │  带 redirect 参数     │             │  Apply.vue              │
           └──────────┬────────────┘             └────────────┬────────────┘
                      │                                         │
                      ▼                                         │
           [登录成功后自动跳回] ─────────────────────────────────┘
                                         │
                                         ▼
                           ┌───────────────────────────┐
                           │     2. 填写申请表           │
                           │   Apply.vue:37-158          │
                           │   9个字段 + 前端校验          │
                           └─────────────┬───────────────┘
                                         │
                                         ▼ 点击"提交申请"
                           ┌───────────────────────────┐
                           │     3. 确认弹窗             │
                           │   Modal 组件                │
                           └─────────────┬───────────────┘
                                         │
                                         ▼ 确认提交
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    前端 API 调用                                                   │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                    ┌──────────────────────────────────────────────────────┐
                    │  adoptionApi.apply({                                 │
                    │    petId, livingType, housingType, hasYard,          │
                    │    experience, dailyTime, currentPets,               │
                    │    reason, contactAddress                            │
                    │  }) → POST /api/adoption/apply                        │
                    └──────────────────────────────────────────────────────┘
                                         │
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    后端处理                                                        │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                                         ▼
                      ┌────────────────────────────────────────┐
                      │  AdoptionController.apply()             │
                      │  controller/AdoptionController.java:26  │
                      │  从 UserContext 取 userId                │
                      └───────────────────┬────────────────────┘
                                          │
                                          ▼
                ┌────────────────────────────────────────────────────┐
                │  AdoptionServiceImpl.apply()                        │
                │  service/impl/AdoptionServiceImpl.java:29-90        │
                └─────────────┬─────────────────────┬────────────────┘
                              │ 校验                 │ 插入
                              ▼                     ▼
        ┌────────────────────────────┐   ┌─────────────────────────┐
        │ 1. 校验宠物存在且可领养     │   │ 构建 AdoptionApplication │
        │    pet.status === 1        │   │ status = 0 (待审核)      │
        │ 2. 检查重复申请             │   │ currentPets 转逗号分隔   │
        │ 3. 校验9个必填字段          │   └──────────┬──────────────┘
        └──────────────┬─────────────┘              │
                       │ 校验通过                     │
                       └──────────────────────────────┘
                                          │
                                          ▼
                         ┌──────────────────────────────────┐
                         │  AdoptionMapper.insert()          │
                         │  mapper/AdoptionMapper.xml:24-29  │
                         │  INSERT INTO adoption_application │
                         └──────────────────┬───────────────┘
                                            │
                                            ▼
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    数据库                                                          │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                      ┌──────────────────────────────────────────┐
                      │  表: adoption_application                  │
                      │  字段:                                     │
                      │    id, user_id, pet_id, living_type,       │
                      │    housing_type, has_yard, experience,     │
                      │    current_pets, daily_time, reason,       │
                      │    contact_address, status=0,              │
                      │    create_time, update_time                │
                      └──────────────────────────────────────────┘
                                         │
                                         ▼ 返回 applicationId
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    前端响应处理                                                    │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
                                         │
                                         ▼
                    ┌────────────────────────────────────────────┐
                    │  Apply.vue:288-290                          │
                    │  toast.success('申请提交成功！')            │
                    │  router.push('/user/applications')         │
                    └────────────────────┬───────────────────────┘
                                         │
                                         ▼
                           ┌───────────────────────────┐
                           │  4. 申请列表页              │
                           │  Applications.vue          │
                           └─────────────┬──────────────┘
                                         │
                                         ▼
              ┌───────────────────────────────────────────────────────┐
              │  adoptionApi.getList({ page, pageSize, status })       │
              │  → GET /api/adoption/list                               │
              └───────────────────────────────────────────────────────┘
                                         │
                                         ▼
              ┌───────────────────────────────────────────────────────┐
              │  AdoptionController.getMyApplications()                │
              │  → AdoptionServiceImpl.getMyApplications()             │
              │  → AdoptionMapper.selectByUserId()                     │
              │    LEFT JOIN pet 获取宠物名称、图片                     │
              └───────────────────────────────────────────────────────┘
                                         │
                                         ▼
                           ┌───────────────────────────┐
                           │  渲染申请列表               │
                           │  展示状态标签、分页           │
                           └───────────────────────────┘
```

---

## 二、状态定义

### 申请状态 (adoption_application.status)

| 状态值 | 状态文本 | 含义 |
|--------|---------|------|
| 0 | 待审核 | 刚提交，等待管理员审核 |
| 1 | 审核中 | 管理员正在审核 |
| 2 | 已通过 | 审核通过，可预约面谈 |
| 3 | 已拒绝 | 审核不通过 |
| 4 | 已取消 | 用户主动取消 |

### 宠物状态 (pet.status)

| 状态值 | 含义 |
|--------|------|
| 0 | 下架 |
| 1 | 可领养 |
| 2 | 已被领养 |

---

## 三、分步详解

### 阶段 1：进入详情页并点击申请

**前端文件**：`frontend/src/views/PetDetail.vue`

**关键代码位置**：第 108-131 行

```javascript
// PetDetail.vue:110-125
<template v-if="pet.status === 1">
  <router-link
    v-if="isLoggedIn"
    :to="`/apply/${pet.id}`"
    class="block w-full btn-main text-center"
  >
    申请领养
  </router-link>
  <router-link
    v-else
    :to="{ name: 'Login', query: { redirect: `/apply/${pet.id}` } }"
    class="block w-full btn-main text-center"
  >
    登录后申请领养
  </router-link>
</template>
```

**说明**：
- 只有当宠物状态 `pet.status === 1`（可领养）时，才显示申请按钮
- 已登录用户直接跳转到 `/apply/{petId}`
- 未登录用户跳转到登录页，携带 `redirect` 参数，登录成功后自动跳回申请页

---

### 阶段 2：填写申请表单

**前端文件**：`frontend/src/views/Apply.vue`

**表单字段**（共 9 项）：

| 字段名 | 类型 | 必填 | 校验规则 | 对应数据库字段 |
|--------|------|------|---------|---------------|
| livingType | 单选 | ✅ | 枚举: 独居/家人同住/合租 | living_type |
| housingType | 单选 | ✅ | 枚举: 自有房产/租房 | housing_type |
| hasYard | 布尔 | ✅ | 是/否 | has_yard |
| experience | 下拉 | ✅ | 无经验/1-3年/3年以上 | experience |
| dailyTime | 下拉 | ✅ | 少于2小时/2-5小时/5小时以上 | daily_time |
| currentPets | 多选 | ❌ | 猫/狗/其他/无 | current_pets |
| reason | 文本域 | ✅ | 50-500字 | reason |
| contactAddress | 文本 | ✅ | 至少5个字符 | contact_address |

**前端校验逻辑**（第 226-237 行）：

```javascript
const isFormValid = computed(() => {
  return (
    form.livingType &&
    form.housingType &&
    form.hasYard !== null &&
    form.experience &&
    form.dailyTime &&
    form.reason.length >= 50 &&
    form.reason.length <= 500 &&
    form.contactAddress.length >= 5
  )
})
```

---

### 阶段 3：提交申请 - 前端 API 调用

**前端文件**：`frontend/src/views/Apply.vue:270-299`

**API 封装**：`frontend/src/api/adoption.js:5-7`

```javascript
// Apply.vue:276-286
const res = await adoptionApi.apply({
  petId: parseInt(petId),
  livingType: form.livingType,
  housingType: form.housingType,
  hasYard: form.hasYard ? 1 : 0,
  experience: form.experience,
  dailyTime: form.dailyTime,
  currentPets: form.currentPets,
  reason: form.reason,
  contactAddress: form.contactAddress
})

// 成功后跳转
if (res.code === 200) {
  toast.success('申请提交成功！')
  router.push('/user/applications')
}
```

**请求详情**：
- Method: `POST`
- URL: `/api/adoption/apply`
- Header: 自动携带 JWT Token（通过拦截器）
- Body: JSON 格式

---

### 阶段 4：提交申请 - 后端 Controller

**后端文件**：`backend/src/main/java/com/petadoption/controller/AdoptionController.java:26-34`

```java
@PostMapping("/apply")
public Result<Map<String, Long>> apply(@RequestBody AdoptionApplyDTO dto) {
    Long userId = UserContext.getUserId();          // 从登录上下文取当前用户ID
    Long applicationId = adoptionService.apply(userId, dto);

    Map<String, Long> result = new HashMap<>();
    result.put("applicationId", applicationId);
    return Result.success("申请提交成功", result);
}
```

**说明**：
- `UserContext.getUserId()` 从 JWT Token 中解析用户 ID（通过 LoginInterceptor 拦截器设置）
- 调用 Service 层执行业务逻辑
- 返回新创建的申请 ID

---

### 阶段 5：提交申请 - 后端 Service 校验

**后端文件**：`backend/src/main/java/com/petadoption/service/impl/AdoptionServiceImpl.java:29-67`

**三层校验**：

#### 5.1 宠物状态校验（第 32-39 行）

```java
Pet pet = petMapper.selectById(dto.getPetId());
if (pet == null) {
    throw BusinessException.notFound("宠物不存在");
}
if (pet.getStatus() != 1) {
    throw BusinessException.badRequest("该宠物已被领养或下架");
}
```

#### 5.2 重复申请校验（第 41-44 行）

```java
if (adoptionMapper.existsByUserIdAndPetId(userId, dto.getPetId())) {
    throw BusinessException.conflict("您已提交过该宠物的领养申请");
}
```

**对应 SQL**（AdoptionMapper.xml:57-60）：
```sql
SELECT COUNT(1) > 0 FROM adoption_application
WHERE user_id = #{userId} AND pet_id = #{petId} AND status IN (0, 1, 2)
```
> 注意：只检查状态为 0（待审核）、1（审核中）、2（已通过）的申请，已拒绝/已取消的可以重新申请

#### 5.3 字段合法性校验（第 46-67 行）

共 7 项字段校验，与前端保持一致。

---

### 阶段 6：提交申请 - 数据库插入

**后端文件**：`backend/src/main/java/com/petadoption/service/impl/AdoptionServiceImpl.java:69-89`

```java
// 构建实体对象
AdoptionApplication application = new AdoptionApplication();
application.setUserId(userId);
application.setPetId(dto.getPetId());
application.setLivingType(dto.getLivingType());
application.setHousingType(dto.getHousingType());
application.setHasYard(dto.getHasYard() ? 1 : 0);   // Boolean → Integer
application.setExperience(dto.getExperience());
application.setDailyTime(dto.getDailyTime());
application.setReason(dto.getReason());
application.setContactAddress(dto.getContactAddress());
application.setStatus(0);  // 【关键】初始状态 = 0 (待审核)

// 处理多选字段：List<String> → 逗号分隔字符串
if (dto.getCurrentPets() != null && !dto.getCurrentPets().isEmpty()) {
    application.setCurrentPets(String.join(",", dto.getCurrentPets()));
}

adoptionMapper.insert(application);
return application.getId();  // useGeneratedKeys 返回自增ID
```

**SQL 语句**（AdoptionMapper.xml:24-29）：
```sql
INSERT INTO adoption_application (
    user_id, pet_id, living_type, housing_type, has_yard, experience,
    current_pets, daily_time, reason, contact_address, status,
    create_time, update_time
) VALUES (
    #{userId}, #{petId}, #{livingType}, #{housingType}, #{hasYard}, #{experience},
    #{currentPets}, #{dailyTime}, #{reason}, #{contactAddress}, #{status},
    NOW(), NOW()
)
```

---

### 阶段 7：申请列表加载

**前端文件**：`frontend/src/views/user/Applications.vue:181-203`

**API 调用**：
```javascript
const res = await adoptionApi.getList({
    page: currentPage.value,
    pageSize: 10,
    status: currentStatus.value  // 可选筛选
})
```

**后端处理**（AdoptionServiceImpl.java:92-129）：
1. 分页参数处理（page, pageSize）
2. 按 `create_time DESC` 降序查询 `adoption_application` 表
3. **在 Stream 中循环调用 `petMapper.selectById(app.getPetId())` 逐条查询宠物信息**（N+1 查询）
4. 组装 `PetSimpleVO`（名称、品种、第一张图片等）
5. 返回 `PageResult` 结构：`{ list, total, page, pageSize }`

**注意**：这是 N+1 查询问题。查询 10 条申请会触发 1 次 application 查询 + 10 次 pet 查询，共 11 次数据库交互。

**对应 SQL**（AdoptionMapper.xml:31-39）：
```sql
SELECT * FROM adoption_application
WHERE user_id = #{userId}
<if test="status != null">AND status = #{status}</if>
ORDER BY create_time DESC
LIMIT #{offset}, #{pageSize}
```

---

## 四、状态流转图

```
                  提交申请
    ┌──────────────────────────────────┐
    │                                  │
    ▼                                  │
┌───────┐        管理员审核        ┌───────┐
│ 待审核 │ ─────────────────────► │ 审核中 │
│   0   │                        │   1   │
└───┬───┘                        └───┬───┘
    │ 用户取消                        │
    ▼                                ▼ 管理员通过/拒绝
┌───────┐                        ┌───────┐  ┌───────┐
│ 已取消 │                        │ 已通过 │  │ 已拒绝 │
│   4   │                        │   2   │  │   3   │
└───────┘                        └───────┘  └───────┘

状态说明：
- 用户可操作：提交(0→0)、取消(0→4)
- 管理员可操作：开始审核(0→1)、通过(1→2)、拒绝(1→3)
- 终态：已通过(2)、已拒绝(3)、已取消(4)
```

---

## 五、核心文件索引

### 前端
| 模块 | 文件路径 | 职责 |
|------|---------|------|
| 详情页 | `frontend/src/views/PetDetail.vue:108-131` | 申请入口，判断登录状态 |
| 申请页 | `frontend/src/views/Apply.vue` | 表单填写、校验、提交 |
| 申请列表 | `frontend/src/views/user/Applications.vue` | 列表展示、筛选、取消 |
| API 封装 | `frontend/src/api/adoption.js` | 4 个 API 方法 |
| 路由 | `frontend/src/router/index.js` | `/apply/:petId`, `/user/applications` |

### 后端
| 模块 | 文件路径 | 职责 |
|------|---------|------|
| Controller | `controller/AdoptionController.java` | 接收请求、参数绑定 |
| Service | `service/impl/AdoptionServiceImpl.java` | 业务逻辑、校验、事务 |
| Entity | `entity/AdoptionApplication.java` | 数据库实体映射 |
| DTO | `dto/AdoptionApplyDTO.java` | 申请提交入参 |
| VO | `vo/AdoptionVO.java`, `vo/AdoptionDetailVO.java` | 返回给前端的数据结构 |
| Mapper | `mapper/AdoptionMapper.java` + `.xml` | SQL 映射 |

### 数据库
| 表 | 文件 | 关键字段 |
|----|------|---------|
| adoption_application | `resources/sql/schema.sql:88-111` | status, user_id, pet_id, create_time |
| pet | `resources/sql/schema.sql:56-83` | status, images |
