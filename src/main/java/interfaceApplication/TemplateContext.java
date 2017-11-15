package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;
import Model.CommonModel;
import apps.appsProxy;
import authority.plvDef.plvType;
import interfaceModel.GrapeDBSpecField;
import interfaceModel.GrapeTreeDBModel;
import string.StringHelper;

public class TemplateContext {
    private GrapeTreeDBModel tempContext;
    private GrapeDBSpecField gDbSpecField;
    private CommonModel model;
    private Integer userType = null;
    
    public TemplateContext() {
        model = new CommonModel();
        tempContext = new GrapeTreeDBModel();
        gDbSpecField = new GrapeDBSpecField();
        gDbSpecField.importDescription(appsProxy.tableConfig("TemplateContext"));
        tempContext.descriptionModel(gDbSpecField);
        tempContext.bindApp();
        tempContext.enableCheck();//开启权限检查

    }

    /**
     * 新增模版
     * 
     * @param tempinfo
     * @return
     */
    public String TempInsert(String tempinfo) {
        Object info = null;
        String result = rMsg.netMSG(100, "新增模版失败");
        if (!StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        JSONObject rMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 100);//设置默认查询权限
    	JSONObject uMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 200);
    	JSONObject dMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 300);
    	object.put("rMode", rMode.toJSONString()); //添加默认查看权限
    	object.put("uMode", uMode.toJSONString()); //添加默认修改权限
    	object.put("dMode", dMode.toJSONString()); //添加默认删除权限
        if (object != null && object.size() > 0) {
            info = tempContext.data(object).insertEx();
        }
        return info != null ? rMsg.netMSG(0, "新增模版成功") : result;
    }

    /**
     * 删除模版
     * 
     * @param tempID
     * @return
     */
    public String TempDelete(String tempID) {
        return TempBatchDelete(tempID);
    }

    public String TempBatchDelete(String tempid) {
        long code = 0;
        String[] value = null;
        String result = rMsg.netMSG(100, "删除失败");
        if (StringHelper.InvaildString(tempid)) {
            value = tempid.split(",");
        }
        if (tempid != null) {
            tempContext.or();
            for (String id : value) {
                tempContext.eq("_id", id);
            }
            code = tempContext.deleteAll();
        }
        return code > 0 ? rMsg.netMSG(0, "删除成功") : result;
    }

    /**
     * 更新模版
     * 
     * @param tempid
     * @param tempinfo
     * @return
     */
    public String TempUpdate(String tempid, String tempinfo) {
        Object code = 99;
        String result = rMsg.netMSG(100, "模版更新失败");
        if (!StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        if (object != null && object.size() > 0) {
            code = tempContext.eq("_id", tempid).data(object).updateEx();
        }
        return code != null ? rMsg.netMSG(0, "模版更新成功") : result;
    }

    /**
     * 分页显示模版
     * 
     * @param idx
     * @param pageSize
     * @return
     */
    public String TempPage(int idx, int pageSize) {
        return TempPageBy(idx, pageSize, null);
    }

    public String TempPageBy(int idx, int pageSize, String tempinfo) {
        long total = 0;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray condArray = model.buildCond(tempinfo);
            if (condArray != null && condArray.size() > 0) {
                tempContext.where(condArray);
            } else {
                return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
            }
        }
        JSONArray array = tempContext.dirty().page(idx, pageSize);
        total = tempContext.count();
        return rMsg.netPAGE(idx, pageSize, total, (array != null && array.size() > 0) ? array : new JSONArray());
    }

    /**
     * 根据类型查询模版信息
     * 
     * @param tempinfo
     * @return
     */
    public String TempFindByType(String tempinfo) {
        JSONArray array = null;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray condArray = model.buildCond(tempinfo);
            if (condArray != null && condArray.size() > 0) {
                array = tempContext.where(condArray).select();
            }
        }
        return rMsg.netMSG(true, (array != null && array.size() > 0) ? array : new JSONArray());
    }

    /**
     * 获取模版名称
     * @param tids
     * @return {tid:name,tid:name}
     */
    @SuppressWarnings("unchecked")
    public String TempFindByTid(String tids) {
        String[] value = null;
        JSONArray array = null;
        JSONObject rObject = null, tempObj;
        if (StringHelper.InvaildString(tids)) {
            value = tids.split(",");
        }
        if (value != null) {
            tempContext.or();
            for (String tid : value) {
                tempContext.eq("_id", tid);
            }
            array = tempContext.field("_id,name").select();
        }
        if (array != null && array.size() > 0) {
            rObject = new JSONObject();
            for (Object object : array) {
                tempObj = (JSONObject) object;
                rObject.put(tempObj.getMongoID("_id"), tempObj.getString("name"));
            }
        }
        return (rObject != null && rObject.size() > 0) ? rObject.toJSONString() : null;
    }
}
