# springboot oauth2 security 单点登录中心的深度应用
##  场景
1. 登录的用户token是general
2. 各个应用子系统有自己的用户权限角色
怎么破？

## 思路
 1. 登录的用户中心称为General SSO
 2. 各个应用平台的子系统视情况构建自己的SSO， 如果是单进程则只需构建自己的UserDetailService(spring熟练的自然知道这个service是啥东东)
 3. 登录General SSO 获得用户的基本信息(user global id 等)
 4. 用该token 访问各个子系统的资源
 5. 子系统怎么判断该用户的权限角色？
	首先子系统的用户信息资源应该可以关联该user global id, 得到用户的权限角色。
	然后子系统获得到该用户的权限角色的信息后放在子系统生成的token里面， 替换原token
	最后：子系统将子系统的user authentication 更新在自己的spring security context里面(这里spirng 熟练的自然知道指啥东东)


## 操作
前面第5步怎么做？
提示： spring 里面的Filter 可以用来做这些偷天换日的操作！