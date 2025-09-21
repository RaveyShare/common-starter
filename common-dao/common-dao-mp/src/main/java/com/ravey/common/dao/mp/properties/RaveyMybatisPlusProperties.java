
package com.ravey.common.dao.mp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mybatis-plus")
public class RaveyMybatisPlusProperties {
    public static final String PREFIX = "mybatis-plus";
    private boolean enableMybatisPlusObjectHandler = true;
    private boolean enableInjectUserWithJobNumber = false;
    private Plugin plugin = new Plugin();

    public boolean isEnableMybatisPlusObjectHandler() {
        return this.enableMybatisPlusObjectHandler;
    }

    public boolean isEnableInjectUserWithJobNumber() {
        return this.enableInjectUserWithJobNumber;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void setEnableMybatisPlusObjectHandler(boolean enableMybatisPlusObjectHandler) {
        this.enableMybatisPlusObjectHandler = enableMybatisPlusObjectHandler;
    }

    public void setEnableInjectUserWithJobNumber(boolean enableInjectUserWithJobNumber) {
        this.enableInjectUserWithJobNumber = enableInjectUserWithJobNumber;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RaveyMybatisPlusProperties)) {
            return false;
        }
        RaveyMybatisPlusProperties other = (RaveyMybatisPlusProperties)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isEnableMybatisPlusObjectHandler() != other.isEnableMybatisPlusObjectHandler()) {
            return false;
        }
        if (this.isEnableInjectUserWithJobNumber() != other.isEnableInjectUserWithJobNumber()) {
            return false;
        }
        Plugin this$plugin = this.getPlugin();
        Plugin other$plugin = other.getPlugin();
        return !(this$plugin == null ? other$plugin != null : !this$plugin.equals(other$plugin));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RaveyMybatisPlusProperties;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEnableMybatisPlusObjectHandler() ? 79 : 97);
        result = result * 59 + (this.isEnableInjectUserWithJobNumber() ? 79 : 97);
        Plugin $plugin = this.getPlugin();
        result = result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
        return result;
    }

    public String toString() {
        return "BcMybatisPlusProperties(enableMybatisPlusObjectHandler=" + this.isEnableMybatisPlusObjectHandler() + ", enableInjectUserWithJobNumber=" + this.isEnableInjectUserWithJobNumber() + ", plugin=" + this.getPlugin() + ")";
    }

    public static class Plugin {
        private boolean enablePaginationInterceptor = true;
        private boolean enableOptimisticLockerInterceptor = true;
        private boolean enableDynamicTableNameInnerInterceptor = false;

        public boolean isEnablePaginationInterceptor() {
            return this.enablePaginationInterceptor;
        }

        public boolean isEnableOptimisticLockerInterceptor() {
            return this.enableOptimisticLockerInterceptor;
        }

        public boolean isEnableDynamicTableNameInnerInterceptor() {
            return this.enableDynamicTableNameInnerInterceptor;
        }

        public void setEnablePaginationInterceptor(boolean enablePaginationInterceptor) {
            this.enablePaginationInterceptor = enablePaginationInterceptor;
        }

        public void setEnableOptimisticLockerInterceptor(boolean enableOptimisticLockerInterceptor) {
            this.enableOptimisticLockerInterceptor = enableOptimisticLockerInterceptor;
        }

        public void setEnableDynamicTableNameInnerInterceptor(boolean enableDynamicTableNameInnerInterceptor) {
            this.enableDynamicTableNameInnerInterceptor = enableDynamicTableNameInnerInterceptor;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Plugin)) {
                return false;
            }
            Plugin other = (Plugin)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.isEnablePaginationInterceptor() != other.isEnablePaginationInterceptor()) {
                return false;
            }
            if (this.isEnableOptimisticLockerInterceptor() != other.isEnableOptimisticLockerInterceptor()) {
                return false;
            }
            return this.isEnableDynamicTableNameInnerInterceptor() == other.isEnableDynamicTableNameInnerInterceptor();
        }

        protected boolean canEqual(Object other) {
            return other instanceof Plugin;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + (this.isEnablePaginationInterceptor() ? 79 : 97);
            result = result * 59 + (this.isEnableOptimisticLockerInterceptor() ? 79 : 97);
            result = result * 59 + (this.isEnableDynamicTableNameInnerInterceptor() ? 79 : 97);
            return result;
        }

        public String toString() {
            return "BcMybatisPlusProperties.Plugin(enablePaginationInterceptor=" + this.isEnablePaginationInterceptor() + ", enableOptimisticLockerInterceptor=" + this.isEnableOptimisticLockerInterceptor() + ", enableDynamicTableNameInnerInterceptor=" + this.isEnableDynamicTableNameInnerInterceptor() + ")";
        }
    }
}