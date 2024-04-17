package org.tinycloud.paginate.dialect.support;

import org.tinycloud.paginate.Page;
import org.tinycloud.paginate.dialect.AbstractDialect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SybaseDialect extends AbstractDialect {
    private final boolean hasTop;

    public SybaseDialect() {
        this(false);
    }

    public SybaseDialect(boolean hasTop) {
        this.hasTop = hasTop;
    }

    /**
     * 分页查询适配
     *
     * @param originalSql 原始sql
     * @param page        分页参数对象
     * @return 处理过后的sql
     */
    @Override
    public String getPageSql(String originalSql, Page<?> page) {
        long pageNo = page.getPageNum();
        long pageSize = page.getPageSize();

        long offset = (pageNo - 1L) * pageSize;
        long limit = pageSize;

        int index = this.findMainFROM(originalSql);
        if (index == -1) {
            index = originalSql.toUpperCase().indexOf(" FROM ");
        }
        String sql = "select";
        if (this.hasTop) {
            sql = sql + " top " + (offset + limit);
        }
        sql = sql + " rownum=identity(12)," + originalSql.substring(6, index) + " into #t " + originalSql.substring(index);
        sql = sql + " select * from #t where rownum > " + offset + " and rownum <= " + (offset + limit);
        sql = sql + " drop table #t ";
        return sql;
    }


    private int findMainFROM(String sql) {
        String tempSql = sql.toUpperCase();
        tempSql = tempSql.replace("\n", " ").replace("\t", " ").replace("\r", " ");
        Matcher select_ = Pattern.compile("SELECT ").matcher(tempSql);
        Matcher from_ = Pattern.compile(" FROM ").matcher(tempSql);
        List<Integer> selectIndex = new ArrayList<>(10);
        ArrayList<Integer> fromIndex = new ArrayList<>(10);

        while (true) {
            int start;
            do {
                if (!select_.find()) {
                    while (from_.find()) {
                        fromIndex.add(from_.start());
                    }

                    List<Integer> indexList = new ArrayList(20);
                    indexList.addAll(selectIndex);
                    indexList.addAll(fromIndex);
                    indexList.sort(Comparator.naturalOrder());
                    if (indexList.size() < 2) {
                        return -1;
                    }

                    int selectCount = 1;

                    for (int i = 1; i < indexList.size(); ++i) {
                        int each = (Integer) indexList.get(i);
                        if (fromIndex.contains(each)) {
                            --selectCount;
                        } else {
                            ++selectCount;
                        }

                        if (selectCount == 0) {
                            return each;
                        }
                    }

                    return -1;
                }

                start = select_.start();
            } while (start != 0 && tempSql.charAt(start - 1) != ' ' && tempSql.charAt(start - 1) != '(');

            selectIndex.add(start);
        }
    }
}
