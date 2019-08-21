# springboot oauth2 security 单点登录中心的深度应用
##  场景
1. 登录的用户token是general
2. 各个应用子系统有自己的用户权限角色


## 思路
 1. 登录的用户中心称为General SSO
 2. 各个应用平台的子系统视情况构建自己的SSO， 如果是单进程则只需构建自己的UserDetailService
 3. 登录General SSO 获得用户的基本信息(user global id 等)
 4. 用该token 访问各个子系统的资源
 5. 子系统怎么判断该用户的权限角色？
	首先子系统的用户信息资源应该可以关联该user global id, 得到用户的权限角色。
	然后子系统获得到该用户的权限角色的信息后放在子系统生成的token里面， 替换原token
	最后：子系统将子系统的user authentication 更新在自己的spring security context里面


## 操作
前面第5步用 spring 里面的Filter 可以用来做这偷天换日的操作。


## 领域设计
### 用户公共属性
#### 用户个人属性
1. 姓名(中文, 英文, 拼音) name, cname, ename, pinying
2. 电话号码 mobile_number
3. 邮箱 email
4. 登录密码 password
5. 别名 alias
6. 用户状态 - locked, unlocked

#### 用户公司属性
1. 部门 dept -- dept 部门编码 id
2. 地区 location -- location 编码id
3. 小组 group -- 小组序号
4. 职位 title -- title 编码 id

#### 用户公共属性角色
1. system_admin
2. amc_cmpy_checker
3. amc_location_checker
4. amc_group_admin
5. amc_user

#### 公司
视情况需要建表
#### 部门
视情况需要建表
#### 地区
视情况需要建表
#### 小组
视情况需要建表