package com.permission.utils.ip2region;


import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * <pre>
 * Title : ip查询库
 * </pre>
 *
 * @author xiashilong
 * @since 2022-08-05
 **/
public class Ip2RegionUtil {

    private static final Logger logger = LoggerFactory.getLogger(Ip2RegionUtil.class);


    private Ip2RegionUtil() {
    }


    private static Searcher searcher = null;

    private static Searcher getSearcher() {
        if (searcher == null) {
            try {
                byte[] cBuff = Searcher.loadContentFromFile(Objects.requireNonNull(Ip2RegionUtil.class.getResource("/cn/qi/util/ip2region/ip2region.xdb")).getPath());
                searcher = Searcher.newWithBuffer(cBuff);
            } catch (IOException e) {
                logger.error("ip2region searcher 初始化失败！");
            }
        }
        return searcher;
    }

    /**
     * 根据ip解析
     *
     * @param ip 正确的ip地址
     * @return 地址
     */
    public static Ip2Region search(String ip) {
        Ip2Region ip2Region = new Ip2Region();
        if (!StringUtils.isEmpty(ip)) {
            return ip2Region;
        }
        try {
            Searcher searcher = getSearcher();
            if (null != searcher) {
                return new Ip2Region(searcher.search(ip));
            }
        } catch (Exception e) {
            logger.error("ip2region 查询异常 -> {}", e);
        }
        return ip2Region;
    }

    public static class Ip2Region {

        private final String country;

        private final String province;

        private final String city;

        private final String operator;

        private Ip2Region(String content) {
            logger.debug(content);
            String[] split = content.split("\\|");
            this.country = split.length > 0 ? split[0] : "";
            this.province = split.length > 2 ? split[2] : "";
            this.city = split.length > 3 ? split[3] : "";
            this.operator = split.length > 4 ? split[4] : "";
        }

        public Ip2Region() {
            this.country = "";
            this.province = "";
            this.city = "";
            this.operator = "";
        }

        @Override
        public String toString() {
            return "Ip2Region{" +
                    "country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", operator='" + operator + '\'' +
                    '}';
        }

        public String getCountry() {
            return country;
        }

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getOperator() {
            return operator;
        }
    }
}
