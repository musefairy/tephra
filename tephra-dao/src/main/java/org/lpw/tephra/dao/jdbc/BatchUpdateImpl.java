package org.lpw.tephra.dao.jdbc;

import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.batch-update")
public class BatchUpdateImpl implements BatchUpdate {
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Sql sql;
    private ThreadLocal<Set<String>> tlIgnore = new ThreadLocal<>();
    private ThreadLocal<List<String>> tlDataSource = new ThreadLocal<>();
    private ThreadLocal<List<String>> tlSql = new ThreadLocal<>();
    private ThreadLocal<List<Object[]>> tlArgs = new ThreadLocal<>();

    @Override
    public void begin() {
        tlIgnore.set(new HashSet<>());
        tlDataSource.set(new ArrayList<>());
        tlSql.set(new ArrayList<>());
        tlArgs.set(new ArrayList<>());
    }

    @Override
    public void ignore(String sql) {
        Set<String> set = tlIgnore.get();
        if (set != null)
            set.add(sql);
    }

    @Override
    public boolean collect(String dataSource, String sql, Object[] args) {
        if (tlDataSource.get() == null)
            return false;

        for (String string : tlIgnore.get())
            if (sql.contains(string))
                return false;

        tlDataSource.get().add(dataSource);
        tlSql.get().add(sql);
        tlArgs.get().add(args);
        if (logger.isDebugEnable())
            logger.debug("收集SQL[{}:{}:{}]。", dataSource, sql, converter.toString(args));

        return true;
    }

    @Override
    public void commit() {
        if (tlDataSource.get() == null)
            return;

        List<String> sqls = tlSql.get();
        try {
            long time = System.currentTimeMillis();
            sql.update(tlDataSource.get(), sqls, tlArgs.get());
            if (logger.isDebugEnable())
                logger.debug("批量执行收集的SQL[{}:{}]。", converter.toString(sqls), System.currentTimeMillis() - time);
        } catch (Throwable throwable) {
            logger.warn(throwable, "批量执行收集的SQL[{}]时发生异常！", converter.toString(sqls));
        } finally {
            cancel();
        }
    }

    @Override
    public void cancel() {
        tlIgnore.remove();
        tlDataSource.remove();
        tlSql.remove();
        tlArgs.remove();
    }
}
