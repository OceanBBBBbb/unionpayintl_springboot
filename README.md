# unionpayintl_springboot
银联国际securepay示例代码的springboot版，纯后端版


### 原代码
原先的代码是银联国际官网的 [https://developer.unionpayintl.com/cjweb/api/detail?apiSvcId=4#api-fqa]
一个示例代码：https://developer.unionpayintl.com/cjweb/support/file/download/23?dis=2&apiSvcId=4

采用的是jdk1.6版本，servlet的架构，这里没有要前端jsp代码。

### springboot版
改装后的版本就是基于springboot的，出后端版本的（部分内容）。
入口方式就是直接浏览器访问，比如请求支付：
`http://localhost:8899/api/purchase?orderId=20190919190001&txnTime=20190919190001&txnAmt=10

为了方便测试，采用了get方式
