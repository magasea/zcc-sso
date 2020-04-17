# springboot oauth2 security 单点登录中心的应用
##  场景
1. 登录的用户token是general
2. 各个应用子系统有自己的用户权限角色


## 操作流程
 1. 登录的用户中心称为General SSO
 2. 各个应用平台的子系统视情况构建自己的SSO， 如果是单进程则只需构建自己的UserDetailService
 3. 登录General SSO 获得用户的基本信息(user global id 等), 放在springboot jwt token里，在这里称之为
    global token
 4. 用global token 访问各个子系统的资源
 5. 子系统怎么判断该用户的权限角色？
	首先子系统的用户信息资源应该可以关联该user global id, 得到用户的权限角色。
	如果子系统是单模块进程的， 这种情况很简单， 通过该 user global id查询自己用户系统， 生成
	user authentication 然后更新springboot security context里面的user authentication
	如果子系统是多模块进程的， 利用子系统的sso， 统一管理用户系统
	然后子系统获得到该用户的权限角色的信息后放在子系统生成的token里面， 各个子系统模块根据子系统sso
	生成的token来更新子系统模块的user authentication


## 技术细节
前面第5步用 spring 里面的Filter 可以用来做这偷天换日的操作。
其他技术请查阅springboot security 里的jwttoken 技术文档

## 领域设计
### 用户公共属性
#### 用户个人属性
1. 姓名(中文, 英文, 拼音) name, cname, ename, pinying
2. 电话号码 mobile_number
3. 邮箱 email
4. 登录密码 password
5. 别名 alias
6. 用户状态 - invalid, valid, ...

#### 用户公司属性
1. 部门 dept -- dept 部门编码 id
2. 地区 area -- area 编码id
3. 小组 group -- 小组序号
4. 职位 title -- title 编码 id

#### 用户公共属性角色
1. role_sso_sys_adm 系统管理员 -- 对应技术后台管理
2. role_sso_mgr 公司高管 -- 总/副总经理
3. role_sso_ldr 公司组长 -- 地区小组
4. role_sso_staff 公司业务员 -- 地区小组组员
5. role_sso_partner 公司合作伙伴

#### sso 用户权限
1. perm_cmpy_crud 公司范围增删改查 -- 对应技术后台管理和公司管理者
2. perm_jd 尽调工具
3. perm_zcc 债查查
4. perm_market 存量数据
5. perm_analysis 拍卖分析

#### 公司
文盛: wenshengamc
其他合作公司：
#### 部门
枚举值
#### 地区
枚举值

### 用户角色权限关联逻辑
根据下面2个属性，关联出对应的权限
1. 用户角色
2. 用户部门，级别属性
