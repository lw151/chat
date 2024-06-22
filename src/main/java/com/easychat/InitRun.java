package com.easychat;

import com.easychat.websocket.netty.NettyWebSocketStarter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@Component("initRun")
@Slf4j
public class InitRun implements ApplicationRunner {


    @Resource
    private DataSource dataSource;

    @Resource
    private NettyWebSocketStarter nettyWebSocketStarter;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dataSource.getConnection();
            new Thread(nettyWebSocketStarter).start();
            log.info("服务启动成功");
        } catch (SQLException e) {
            log.error("数据库配置错误，请检查数据库配置");
        } catch (Exception e) {
            log.error("服务启动失败", e);
        }
    }
}
