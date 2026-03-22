package wang.seamas.scratch.web.sm.context;

/**
 * 加密上下文
 * <p>
 * 使用 ThreadLocal 存储当前请求的加密相关信息，包括：
 * - SM4 密钥（十六进制字符串）
 * - SM4 初始化向量（十六进制字符串）
 * - 是否加密请求标识
 * </p>
 * <p>
 * 注意：需要在请求处理完成后清理 ThreadLocal，避免内存泄漏
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class CryptoContext {

    private static final ThreadLocal<CryptoInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 加密信息
     */
    public static class CryptoInfo {
        private final String sm4Key;
        private final String sm4Iv;
        private final boolean encrypted;

        public CryptoInfo(String sm4Key, String sm4Iv) {
            this.sm4Key = sm4Key;
            this.sm4Iv = sm4Iv;
            this.encrypted = true;
        }

        public CryptoInfo() {
            this.sm4Key = null;
            this.sm4Iv = null;
            this.encrypted = false;
        }

        public String getSm4Key() {
            return sm4Key;
        }

        public String getSm4Iv() {
            return sm4Iv;
        }

        public boolean isEncrypted() {
            return encrypted;
        }
    }

    /**
     * 设置加密信息
     *
     * @param sm4Key SM4 密钥（十六进制字符串）
     * @param sm4Iv  SM4 初始化向量（十六进制字符串）
     */
    public static void set(String sm4Key, String sm4Iv) {
        CONTEXT.set(new CryptoInfo(sm4Key, sm4Iv));
    }

    /**
     * 设置非加密请求
     */
    public static void setUnencrypted() {
        CONTEXT.set(new CryptoInfo());
    }

    /**
     * 获取加密信息
     *
     * @return CryptoInfo 对象，如果不存在返回非加密状态
     */
    public static CryptoInfo get() {
        CryptoInfo info = CONTEXT.get();
        return info != null ? info : new CryptoInfo();
    }

    /**
     * 检查当前请求是否加密
     *
     * @return true 如果请求是加密的
     */
    public static boolean isEncrypted() {
        CryptoInfo info = CONTEXT.get();
        return info != null && info.isEncrypted();
    }

    /**
     * 获取 SM4 密钥
     *
     * @return SM4 密钥，如果未设置返回 null
     */
    public static String getSm4Key() {
        CryptoInfo info = CONTEXT.get();
        return info != null ? info.getSm4Key() : null;
    }

    /**
     * 获取 SM4 初始化向量
     *
     * @return SM4 IV，如果未设置返回 null
     */
    public static String getSm4Iv() {
        CryptoInfo info = CONTEXT.get();
        return info != null ? info.getSm4Iv() : null;
    }

    /**
     * 清理上下文
     * <p>
     * 必须在请求处理完成后调用，避免内存泄漏
     * </p>
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
