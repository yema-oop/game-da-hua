//package org.come.protocol;
//
//import com.gl.service.GameService;
//import come.tool.Role.PartJade;
//import come.tool.Role.RoleData;
//import come.tool.Role.RolePool;
//import come.tool.Stall.AssetUpdate;
//import io.netty.channel.ChannelHandlerContext;
//import java.math.BigDecimal;
//import java.util.List;
//import org.come.action.IAction;
//import org.come.bean.LoginResult;
//import org.come.bean.XXGDBean;
//import org.come.entity.Goodstable;
//import org.come.entity.Lingbao;
//import org.come.entity.Record;
//import org.come.entity.RoleTable;
//import org.come.handler.SendMessage;
//import org.come.protocol.Agreement;
//import org.come.server.GameServer;
//import org.come.tool.EquipTool;
//import org.come.until.AllServiceUtil;
//import org.come.until.GsonUtil;
//import org.come.until.SplitLingbaoValue;
//
//public class lingquAction implements IAction {
//
//   public void action(ChannelHandlerContext ctx, String message) {
//      int value = 0;
//      if(message != null) {
//         LoginResult roleInfo = (LoginResult)GameServer.getAllLoginRole().get(ctx);
//         RoleData roleData = RolePool.getRoleData(roleInfo.getRole_id());
//         int taskWC = roleData.getTaskWC(888);
//         if(message != null && !message.equals("")) {
//            value = Integer.parseInt(message);
//         }
//
//         String roleName;
//         String goodsId;
//         String num;
//         RoleTable userTable;
//         XXGDBean xxgdBean;
//         BigDecimal id;
//         Goodstable goodstable;
//         StringBuffer buffer;
//         AssetUpdate assetUpdate;
//         long yid;
//         int message1;
//         long sid;
//         String[] sum;
//         int sameGoodstable;
//         int uses;
//         int pz;
//         PartJade jade;
//         String var26;
//         int var27;
//         Lingbao var28;
//         List var29;
//         if(value == 1) {
//            if(taskWC < 20) {
//               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("未达到二十场"));
//               return;
//            }
//
////            if(roleInfo.getTiantilingqu().intValue() != 0) {
////               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你已经领取过了"));
////               return;
////            }
//
//            roleName = roleInfo.getRolename();
//            goodsId = "99999";
//            num = "1";
//            userTable = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(roleName);
//            if(userTable != null) {
//               xxgdBean = new XXGDBean();
//               xxgdBean.setId(goodsId);
//               xxgdBean.setSum(Integer.parseInt(num));
//               id = new BigDecimal(xxgdBean.getId());
//               goodstable = GameServer.getGood(id);
//               if(goodstable == null) {
//                  return;
//               }
//
//               buffer = new StringBuffer();
//               buffer.append("刷物资接口物品id:");
//               buffer.append(goodsId);
//               buffer.append(",");
//               buffer.append(xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               buffer.append(",接收人:");
//               buffer.append(userTable.getRole_id());
//               buffer.append("_");
//               buffer.append(roleName);
//               AllServiceUtil.getRecordService().insert(new Record(4, buffer.toString()));
//               assetUpdate = new AssetUpdate();
//               assetUpdate.setMsg("获得" + xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               goodstable.setRole_id(userTable.getRole_id());
//               yid = id.longValue();
//
//               for(message1 = 0; message1 < xxgdBean.getSum(); ++message1) {
//                  if(message1 != 0) {
//                     goodstable = GameServer.getGood(id);
//                  }
//
//                  goodstable.setRole_id(userTable.getRole_id());
//                  sid = goodstable.getGoodsid().longValue();
//                  if(sid >= 70001L && sid <= 70030L) {
//                     var28 = SplitLingbaoValue.addling(goodstable.getGoodsname(), userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  } else if(sid >= 69001L && sid <= 69015L) {
//                     var28 = SplitLingbaoValue.addfa(sid, userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     if(message1 == 0) {
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//                  } else if(goodstable.getType() == 825L) {
//                     if(!goodstable.getValue().equals("")) {
//                        sum = goodstable.getValue().split("\\|");
//                        sameGoodstable = Integer.parseInt(sum[0]);
//                        uses = Integer.parseInt(sum[1]);
//                        pz = Integer.parseInt(sum[2]);
//                        jade = roleData.getPackRecord().setPartJade(sameGoodstable, uses, pz, 1);
//                        assetUpdate.setJade(jade);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                     }
//                  } else if(EquipTool.canSuper(goodstable.getType())) {
//                     var27 = yid == sid?xxgdBean.getSum():1;
//                     var29 = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(userTable.getRole_id(), goodstable.getGoodsid());
//                     if(var29.size() != 0) {
//                        uses = ((Goodstable)var29.get(0)).getUsetime().intValue() + var27;
//                        ((Goodstable)var29.get(0)).setUsetime(Integer.valueOf(uses));
//                        AllServiceUtil.getGoodsTableService().updateGoodRedis((Goodstable)var29.get(0));
//                        assetUpdate.setGood((Goodstable)var29.get(0));
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     } else {
//                        goodstable.setUsetime(Integer.valueOf(var27));
//                        AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                        assetUpdate.setGood(goodstable);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//
//                     if(yid == sid) {
//                        break;
//                     }
//                  } else {
//                     goodstable.setUsetime(Integer.valueOf(1));
//                     AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                     assetUpdate.setGood(goodstable);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  }
//               }
//
//               if(GameServer.getRoleNameMap().get(roleName) != null) {
//                  SendMessage.sendMessageToSlef((ChannelHandlerContext)GameServer.getRoleNameMap().get(roleName), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//               }
//
//               var26 = "#Y[系统消息] #G你获得了#R" + xxgdBean.getSum() + "#G个#Y" + goodstable.getGoodsname();
//               (new GameService()).sendMsgToPlayer(var26, roleName);
//               roleInfo.setTiantilingqu(Integer.valueOf(1));
//               AllServiceUtil.getRoleTableService().updateRoleWhenExit(roleInfo);
//            }
//         } else if(value == 2) {
//            if(taskWC < 1) {
//               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("未达到一场"));
//               return;
//            }
//
//            if(roleInfo.getTiantiyisheng().intValue() != 0) {
//               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你已经领取过了"));
//               return;
//            }
//
//            roleName = roleInfo.getRolename();
//            goodsId = "99997";
//            num = "1";
//            userTable = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(roleName);
//            if(userTable != null) {
//               xxgdBean = new XXGDBean();
//               xxgdBean.setId(goodsId);
//               xxgdBean.setSum(Integer.parseInt(num));
//               id = new BigDecimal(xxgdBean.getId());
//               goodstable = GameServer.getGood(id);
//               if(goodstable == null) {
//                  return;
//               }
//
//               buffer = new StringBuffer();
//               buffer.append("刷物资接口物品id:");
//               buffer.append(goodsId);
//               buffer.append(",");
//               buffer.append(xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               buffer.append(",接收人:");
//               buffer.append(userTable.getRole_id());
//               buffer.append("_");
//               buffer.append(roleName);
//               AllServiceUtil.getRecordService().insert(new Record(4, buffer.toString()));
//               assetUpdate = new AssetUpdate();
//               assetUpdate.setMsg("获得" + xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               goodstable.setRole_id(userTable.getRole_id());
//               yid = id.longValue();
//
//               for(message1 = 0; message1 < xxgdBean.getSum(); ++message1) {
//                  if(message1 != 0) {
//                     goodstable = GameServer.getGood(id);
//                  }
//
//                  goodstable.setRole_id(userTable.getRole_id());
//                  sid = goodstable.getGoodsid().longValue();
//                  if(sid >= 70001L && sid <= 70030L) {
//                     var28 = SplitLingbaoValue.addling(goodstable.getGoodsname(), userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  } else if(sid >= 69001L && sid <= 69015L) {
//                     var28 = SplitLingbaoValue.addfa(sid, userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     if(message1 == 0) {
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//                  } else if(goodstable.getType() == 825L) {
//                     if(!goodstable.getValue().equals("")) {
//                        sum = goodstable.getValue().split("\\|");
//                        sameGoodstable = Integer.parseInt(sum[0]);
//                        uses = Integer.parseInt(sum[1]);
//                        pz = Integer.parseInt(sum[2]);
//                        jade = roleData.getPackRecord().setPartJade(sameGoodstable, uses, pz, 1);
//                        assetUpdate.setJade(jade);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                     }
//                  } else if(EquipTool.canSuper(goodstable.getType())) {
//                     var27 = yid == sid?xxgdBean.getSum():1;
//                     var29 = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(userTable.getRole_id(), goodstable.getGoodsid());
//                     if(var29.size() != 0) {
//                        uses = ((Goodstable)var29.get(0)).getUsetime().intValue() + var27;
//                        ((Goodstable)var29.get(0)).setUsetime(Integer.valueOf(uses));
//                        AllServiceUtil.getGoodsTableService().updateGoodRedis((Goodstable)var29.get(0));
//                        assetUpdate.setGood((Goodstable)var29.get(0));
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     } else {
//                        goodstable.setUsetime(Integer.valueOf(var27));
//                        AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                        assetUpdate.setGood(goodstable);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//
//                     if(yid == sid) {
//                        break;
//                     }
//                  } else {
//                     goodstable.setUsetime(Integer.valueOf(1));
//                     AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                     assetUpdate.setGood(goodstable);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  }
//               }
//
//               if(GameServer.getRoleNameMap().get(roleName) != null) {
//                  SendMessage.sendMessageToSlef((ChannelHandlerContext)GameServer.getRoleNameMap().get(roleName), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//               }
//
//               var26 = "#Y[系统消息] #G你获得了#R" + xxgdBean.getSum() + "#G个#Y" + goodstable.getGoodsname();
//               (new GameService()).sendMsgToPlayer(var26, roleName);
//               roleInfo.setTiantiyisheng(Integer.valueOf(1));
//               AllServiceUtil.getRoleTableService().updateRoleWhenExit(roleInfo);
//            }
//         } else if(value == 3) {
//            if(taskWC < 10) {
//               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("未达到十场"));
//               return;
//            }
//
//            if(roleInfo.getTiantisansheng().intValue() != 0) {
//               SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你已经领取过了"));
//               return;
//            }
//
//            roleName = roleInfo.getRolename();
//            goodsId = "99998";
//            num = "1";
//            userTable = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(roleName);
//            if(userTable != null) {
//               xxgdBean = new XXGDBean();
//               xxgdBean.setId(goodsId);
//               xxgdBean.setSum(Integer.parseInt(num));
//               id = new BigDecimal(xxgdBean.getId());
//               goodstable = GameServer.getGood(id);
//               if(goodstable == null) {
//                  return;
//               }
//
//               buffer = new StringBuffer();
//               buffer.append("刷物资接口物品id:");
//               buffer.append(goodsId);
//               buffer.append(",");
//               buffer.append(xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               buffer.append(",接收人:");
//               buffer.append(userTable.getRole_id());
//               buffer.append("_");
//               buffer.append(roleName);
//               AllServiceUtil.getRecordService().insert(new Record(4, buffer.toString()));
//               assetUpdate = new AssetUpdate();
//               assetUpdate.setMsg("获得" + xxgdBean.getSum() + "个" + goodstable.getGoodsname());
//               goodstable.setRole_id(userTable.getRole_id());
//               yid = id.longValue();
//
//               for(message1 = 0; message1 < xxgdBean.getSum(); ++message1) {
//                  if(message1 != 0) {
//                     goodstable = GameServer.getGood(id);
//                  }
//
//                  goodstable.setRole_id(userTable.getRole_id());
//                  sid = goodstable.getGoodsid().longValue();
//                  if(sid >= 70001L && sid <= 70030L) {
//                     var28 = SplitLingbaoValue.addling(goodstable.getGoodsname(), userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  } else if(sid >= 69001L && sid <= 69015L) {
//                     var28 = SplitLingbaoValue.addfa(sid, userTable.getRole_id());
//                     assetUpdate.setLingbao(var28);
//                     if(message1 == 0) {
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//                  } else if(goodstable.getType() == 825L) {
//                     if(!goodstable.getValue().equals("")) {
//                        sum = goodstable.getValue().split("\\|");
//                        sameGoodstable = Integer.parseInt(sum[0]);
//                        uses = Integer.parseInt(sum[1]);
//                        pz = Integer.parseInt(sum[2]);
//                        jade = roleData.getPackRecord().setPartJade(sameGoodstable, uses, pz, 1);
//                        assetUpdate.setJade(jade);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                     }
//                  } else if(EquipTool.canSuper(goodstable.getType())) {
//                     var27 = yid == sid?xxgdBean.getSum():1;
//                     var29 = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(userTable.getRole_id(), goodstable.getGoodsid());
//                     if(var29.size() != 0) {
//                        uses = ((Goodstable)var29.get(0)).getUsetime().intValue() + var27;
//                        ((Goodstable)var29.get(0)).setUsetime(Integer.valueOf(uses));
//                        AllServiceUtil.getGoodsTableService().updateGoodRedis((Goodstable)var29.get(0));
//                        assetUpdate.setGood((Goodstable)var29.get(0));
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     } else {
//                        goodstable.setUsetime(Integer.valueOf(var27));
//                        AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                        assetUpdate.setGood(goodstable);
//                        AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(xxgdBean.getSum()), Integer.valueOf(-3));
//                     }
//
//                     if(yid == sid) {
//                        break;
//                     }
//                  } else {
//                     goodstable.setUsetime(Integer.valueOf(1));
//                     AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
//                     assetUpdate.setGood(goodstable);
//                     AllServiceUtil.getGoodsrecordService().insert(goodstable, (BigDecimal)null, Integer.valueOf(1), Integer.valueOf(-3));
//                  }
//               }
//
//               if(GameServer.getRoleNameMap().get(roleName) != null) {
//                  SendMessage.sendMessageToSlef((ChannelHandlerContext)GameServer.getRoleNameMap().get(roleName), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//               }
//
//               var26 = "#Y[系统消息] #G你获得了#R" + xxgdBean.getSum() + "#G个#Y" + goodstable.getGoodsname();
//               (new GameService()).sendMsgToPlayer(var26, roleName);
//               roleInfo.setTiantisansheng(Integer.valueOf(1));
//               AllServiceUtil.getRoleTableService().updateRoleWhenExit(roleInfo);
//            }
//         }
//
//      }
//   }
//}