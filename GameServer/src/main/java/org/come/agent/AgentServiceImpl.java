package org.come.agent;

import org.come.mapper.AgentMapper;
import org.come.until.MybatisUntil;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class AgentServiceImpl implements AgentService  {
    private AgentMapper agentMapper;

    public AgentServiceImpl() {
        ApplicationContext ctx = MybatisUntil.getApplicationContext();
        // id为类名且首字母小写才能被自动扫描扫到
        this.agentMapper = (AgentMapper) ctx.getBean("agentMapper");
    }


    @Override
    public List<Agent> selectAll() {
        return agentMapper.selectAll();
    }

    @Override
    public Agent selectById(String id) {
        return agentMapper.selectById(id);
    }

    @Override
    public Boolean deleteById(String id) {
        return agentMapper.deleteById(id);
    }

    @Override
    public Agent selectByUserName(String userName) {
        return agentMapper.selectByUserName(userName);
    }

    @Override
    public Boolean addAgent(Agent agent) {
        return agentMapper.addAgent(agent);
    }


    @Override
    public Boolean upAgentPwd(Agent agent) {
        return agentMapper.upAgentPwd(agent);
    }

    @Override
    public Boolean upAgentXyAndJf(Agent agent) {
        return agentMapper.upAgentXyAndJf(agent);
    }

    @Override
    public Boolean upAgent(Agent agent) {
        return agentMapper.upAgent(agent);
    }



}
