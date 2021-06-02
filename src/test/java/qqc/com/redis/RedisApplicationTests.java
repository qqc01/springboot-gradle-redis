package qqc.com.redis;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
class RedisApplicationTests {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedisTemplate() {
//        System.out.println(redisTemplate);
        redisTemplate.opsForValue().set("name", "qqc");
        System.out.println(stringRedisTemplate.opsForValue().get("name"));

        // List
//        stringRedisTemplate.opsForList().leftPush("name:list", "zhangsan");
//        stringRedisTemplate.opsForList().leftPush("sex", "nan");
//
//        System.out.println(redisTemplate.opsForList().size("name"));
//        stringRedisTemplate.opsForList().remove("name:list", 0, "zhangsan");

        // 删除
//        System.out.println(stringRedisTemplate.delete("name"));


//        System.out.println(redisTemplate);

//        redisTemplate.opsForList().leftPush("list", new Person("zhangsan", 18));
//        Object list = redisTemplate.opsForList().leftPop("list");

//        redisTemplate.opsForList().leftPush("persons", new Person("zhangsan", 18));
//        redisTemplate.opsForList().leftPush("persons", new Person("lisi", 20));

//        System.out.println(redisTemplate.opsForList().size("persons"));
//        redisTemplate.delete("persons");
//        System.out.println(redisTemplate.opsForList().size("persons"));


    }

    @Test
    public void setQuantityOfGoods (){
        // 设置购买额度
        stringRedisTemplate.opsForValue().set("goodsKey:85265602","5");
        String value = stringRedisTemplate.opsForValue().get("goodsKey:85265602");
        System.out.println(value);
    }


    @Test
    public void testLua() {
        /**
         * 扣减库存Lua脚本
         * KEYS[1]:购买额度key
         * KEYS[2]:订单key
         * ARGV[1]:扣减额度
         * ARGV[2]:订单信息
         * -1:库存不足
         * -2:重复下单
         * 大于等于0:剩余额度（扣减之后剩余的额度）
         */
        String STOCK_LUA;
        StringBuilder sb = new StringBuilder();
        sb.append("local goodsKey = KEYS[1];");
        sb.append("local orderKey = KEYS[2];");
        sb.append("local decrNum = ARGV[1];");
        sb.append("local orderValue = ARGV[2];");
        sb.append("local goodsNum = redis.call('get', goodsKey);");
        sb.append("local order = redis.call('get', orderKey);");
//        sb.append("if goodsNum < decrNum then");
        sb.append("if tonumber(goodsNum) < tonumber(decrNum) then");
        sb.append("   return -1;");
        sb.append("end;");
        sb.append("if order == false then");
        sb.append("   redis.call('set', orderKey , orderValue);");
        sb.append("      return redis.call('decrBy', goodsKey, decrNum);");
        sb.append("else");
        sb.append("   return -2;");
        sb.append("end;");
        STOCK_LUA = sb.toString();

        //商品数量key   订单key
        List<String> keys = Arrays.asList("goodsKey:85265602", "OrderKey:457811515");

        String decrNum = String.valueOf(10L);

        String orderValue = "xxx";

        DefaultRedisScript<Long> stringDefaultRedisScript = new DefaultRedisScript<>();
        stringDefaultRedisScript.setResultType(Long.class);
        stringDefaultRedisScript.setScriptText(STOCK_LUA);  // STOCK_LUA对应脚本文本
        Long res = stringRedisTemplate.execute(stringDefaultRedisScript, keys, decrNum, orderValue);
        System.out.println(res);
    }

}
