# COAPClient
>该客户端主要是模拟COAP网页端插件设计，涵盖了官方插件的主要功能，网页端插件地址：https://addons.mozilla.org/en-US/firefox/addon/copper-270430/

#### 在CoAP请求中，Code被定义为CoAP请求方法，这些方法有GET、POST、PUT、DELETE，Obsserve

  - Disvocer
> 查找 *coap://ip:port*下的所有节点，查询结果展示在Spanner控件上，测试本地[java服务器](https://github.com/bysr/CoAPService)，以及Android服务器端，官方服务器延迟太高，不建议使用
- GET
> 用于获得某资源
- POST
> 用于创建某资源，可具体设计
- PUT
> 用于更新某资源
- DELETE
> 用于删除某资源
- Observe/Cancel
> 开启/关闭消息订阅功能

---


- **其他说明**

1. COAP消息类型。 CoAP采用与HTTP协议相同的请求响应工作模式。CoAP协议共有4中不同的消息类型
    1. CON——需要被确认的请求，如果CON请求被发送，那么对方必须做出响应
    2. NON——不需要被确认的请求，如果NON请求被发送，那么对方不必做出回应。
    3. ACK——应答消息，接受到CON消息的响应。

    4. RST——复位消息，当接收者接受到的消息包含一个错误，接受者解析消息或者不再关心发送者发送的内容，那么复位消息将会被发送。
