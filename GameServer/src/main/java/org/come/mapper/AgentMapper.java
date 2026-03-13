package org.come.mapper;

import org.come.agent.Agent;
import org.come.annotation.MyBatisAnnotation;

import java.util.List;

/**
 * @author 叶豪芳
 * @date : 2017年11月21日 下午9:04:29
 */
@MyBatisAnnotation
public interface AgentMapper {


    List <Agent> selectAll();

    Agent selectById(String id);

    Boolean deleteById(String id);

    Agent selectByUserName(String userName);

    Boolean addAgent(Agent agent);

    Boolean upAgentPwd(Agent agent);
    Boolean upAgent(Agent agent);

    Boolean upAgentXyAndJf(Agent agent);
}