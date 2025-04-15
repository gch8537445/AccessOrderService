package com.ipath.orderflowservice.partnerapi.fanjia;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class SignatureUtil {
    /**
     * 指定加密算法为 RSA
     */
    private static final String UTF8 = "UTF-8";
    /**
     * 指定加密算法为 RSA
     */
    private static final String RSA = "RSA";
    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    /**
     * 密钥长度，用来初始化
     */
    private static final int KEY_SIZE = 1024;
    /**
     * 私钥串
     */
    private static final String PRIVATE_KEY_TEXT = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIBOgLK5tif M2/e/Kp3V/DkM59V5eyttkOQS8MHXYpWEO/hF4DCIbhUzWjFYi8XIYRjuHDY67 ylfzS4uTROyPa7HqQjBTzOYOnUo9jqDziX+aRF" + "/kTkNChTNifKqLEEa551iCwA4vxCsa3ddD8Bar65dLk9sUZnqRJr6uMJi8zi9AgMB AAECgYAJDYDnG1NO25VplYNcP4zcOZzh4wBdwA1JCk0SYDAEfJ/+lG+M/RNH S6hBcw3plonDSImVCfN159yCNaIakajoYUmlisNngpmC34rFS3frwmWk1igZGo76sn RpRJzaZbtMO4" + "/+sovZ2YiD/apzccVk4D6+2RflvgeLPh4WeX7VwQJBAPWpg9jecS10MLbT/c0vrYw B1v2OJlxfZY8hNnci8ReEIvBY1TcdVWenJsJjeWy4i9TSsszLcOGVFZQx4z/zBysCQ QCFtLuVkIXZcc" + "/nMQc4GhWM5mKaGUJgfv0drvOBiOyouGhZGiA8ZbIcxjk7F2YkZX5YUs3LcPcZ qpQlWI4bnsu3AkEAvn/+iz0r3MieQhiwVt4jIVAH7MW/v6AGfHCP8OD6vnasNV9ds AiiQufe6Z0D+yg83wOovaEBXx0iB7KL" + "+/o00wJAa5zCGaefQxqqFU5NnCbKT4Qhuhs4ZEfw2tIzpH8a0tTRD13KsjG7gBM+f sPfbs3NObMzdVkvWBYFdGVowuw00wJAUxuwOmmt6Y906bIi1lEIoyKq3OPJkU/R +EmjvDT27B4Xdgqt7/IpLN23+8r31KpCnwRBOXYYbw3tPkEkECEXrw==";
    /**
     * 公钥串
     */
    public static final String PUBLIC_KEY_TEXT = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAToCyubYnzNv3vyqd1fw5DOfVeXsrbZDkEvDB12KVhDv4ReAwiG4VM1oxWIvFyGEY7hw2Ou8pX80uLk0Tsj2ux6kIwU8zmDp1KPY6g84l/mkRf5E5DQoUzYnyqixBGuedYgsAOL8QrGt3XQ/AWq+uXS5PbFGZ6kSa" + "+rjCYvM4vQIDAQAB";
    /**
     * 签名字段
     */
    private static final String SIGN = "sign";

    /**
     * @param
     * @return void
     * @description 生成公私钥信息
     * @date 2020/6/17 4:28 PM
     */
    public static void genKeyPair() {
        try {
            // RSA 算法要求有一个可信任的随机数源
            SecureRandom secureRandom = new SecureRandom();
            // 为 RSA 算法创建一个 KeyPairGenerator 对象
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            // 利用上面的随机数据源初始化这个 KeyPairGenerator 对象
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            // 生成密匙对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 得到公钥
            PublicKey publicKey = keyPair.getPublic();
            // 得到私钥
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyBase64 = Base64.encodeBase64String(publicKey.getEncoded());
            String privateKeyBase64 = Base64.encodeBase64String(privateKey.getEncoded());
            System.out.println();
            System.out.println("publicKeyText:");
            System.out.println(publicKeyBase64.replaceAll("\r|\n", ""));
            System.out.println();
            System.out.println("privateKeyText:");
            System.out.println(privateKeyBase64.replaceAll("\r|\n", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return java.security.PublicKey
     * @description 获取公钥
     * @date 2020/6/17 4:47 PM
     */
    public static PublicKey getPublicKey() {
        PublicKey publicKey = null;
        try {
            byte[] keyBytes = Base64.decodeBase64(PUBLIC_KEY_TEXT);
            X509EncodedKeySpec keySpec = new
                    X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * @param
     * @return java.security.PrivateKey
     * @description 获取私钥
     * @date 2020/6/17 4:47 PM
     */
    public static PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        try {

            byte[] keyBytes =  Base64.decodeBase64(PRIVATE_KEY_TEXT);
            PKCS8EncodedKeySpec keySpec = new
                    PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * @param data       数据
     * @param privateKey 私钥
     * @return java.lang.String
     * @description 签名
     * @date 2020/6/17 5:10 PM
     */
    public static String sign(Map<String, Object> data, PrivateKey privateKey) {
        try {
            String content = mapToString(data);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(content.getBytes(UTF8));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data      数据
     * @param publicKey 公钥
     * @return boolean
     * @description 校验签名
     * @date 2020/6/17 5:10 PM
     */
    public static boolean verifySign(Map<String, Object> data, PublicKey publicKey) {
        try {
            // 原始签名值
            String checkSign = String.valueOf(data.get(SIGN));
            // 需校验的内容
            String content = mapToString(data);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(UTF8));
            return
                    signature.verify(Base64.decodeBase64(checkSign.getBytes(UTF8)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param data map
     * @return java.lang.String
     * @description map 转字符串，需要字典排序
     * @date 2020/6/17 5:03 PM
     */
    public static String mapToString(Map<String, Object> data) {
        Map<String, Object> treeMap = new TreeMap<>(data);
        StringBuilder sb = new StringBuilder(1024);
        treeMap.forEach((k, v) -> {
            if (!SIGN.equals(k) && Objects.nonNull(v) && StringUtils.isNotBlank(String.valueOf(v))) {
                sb.append(k).append('=').append(v).append('&');
            }
        });
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        //genKeyPair();
//        PublicKey publicKey = getPublicKey();
//        PrivateKey privateKey = getPrivateKey();
//        Map<String, Object> data = new HashMap<>();
//        data.put("appId", "testAppId");
//        data.put("nonceStr", RandomStringUtils.randomAlphanumeric(20));
//        Map<String, Object> bizReqMap = new HashMap<>(256);
//        bizReqMap.put("payRequestId", "8888888888888888888888");
//        bizReqMap.put("payAmount", 100);
//        bizReqMap.put("requestTime", "2020-06-17 09:45:37");
//        bizReqMap.put("subject", "测试");
//        bizReqMap.put("notifyUrl", "https://www.baidu.com");
//        data.put("bizReq", JSON.toJSONString(bizReqMap));
//        String sign = sign(data, privateKey);
//        System.out.println("sign = " + sign);
//        data.put(SIGN, sign);
//        System.out.println(JSON.toJSONString(data));
//        boolean verifyResult = verifySign(data, publicKey);
//        System.out.println("verifyResult = " + verifyResult);
//    }


}
