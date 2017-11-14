package interfaceApplication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.rMsg;
import Model.CommonModel;
import apps.appsProxy;
import interfaceModel.GrapeDBSpecField;
import interfaceModel.GrapeTreeDBModel;
import session.session;
import string.StringHelper;

public class TempList {
    private GrapeTreeDBModel temp;
    private GrapeDBSpecField gDbSpecField;
    private CommonModel model;
    private session se;
    private JSONObject userInfo = null;
    private String currentWeb = null;

    public TempList() {
        model = new CommonModel();
        temp = new GrapeTreeDBModel();
        gDbSpecField = new GrapeDBSpecField();
        gDbSpecField.importDescription(appsProxy.tableConfig("TempList"));
        temp.descriptionModel(gDbSpecField);
        temp.bindApp();

        se = new session();
        userInfo = se.getDatas();
        if (userInfo != null && userInfo.size() != 0) {
            currentWeb = userInfo.getString("currentWeb"); // 当前站点id
        }
    }

    /**
     * 新增模版方案
     * 
     * @param tempinfo
     * @return
     */
    @SuppressWarnings("unchecked")
    public String TempListInsert(String tempinfo) {
        int code = 99;
        String result = rMsg.netMSG(100, "新增模版方案失败");
        if (StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject object = JSONObject.toJSON(tempinfo);
        if (object != null && object.size() > 0) {
            if (object.containsKey("wbid")) {
                object.put("wbid", currentWeb);
            }
            code = temp.dataEx(object).insertOnce() != null ? 0 : 99;
        }
        return code == 0 ? rMsg.netMSG(0, "新增模版方案成功") : result;
    }

    /**
     * 更改模版方案
     * 
     * @param tid
     * @param tempinfo
     * @return
     */
    public String TempListUpdate(String tid, String tempinfo) {
        int code = 99;
        String result = rMsg.netMSG(100, "更改模版方案失败");
        if (StringHelper.InvaildString(tempinfo)) {
            return rMsg.netMSG(1, "参数错误");
        }
        JSONObject obj = JSONObject.toJSON(tempinfo);
        code = temp.eq("_id", tid).data(obj).update() != null ? 0 : 99;
        return code == 0 ? rMsg.netMSG(0, "更改模版方案成功") : result;
    }

    /**
     * 删除
     * 
     * @param id
     * @return
     */
    public String TempListDelete(String id) {
        return TempListBatchDelete(id);
    }

    public String TempListBatchDelete(String tid) {
        long code = 0;
        String[] value = null;
        String result = rMsg.netMSG(100, "模版方案删除失败");
        if (!StringHelper.InvaildString(tid)) {
            value = tid.split(",");
        }
        if (value != null) {
            temp.or();
            for (String id : value) {
                temp.eq("_id", id);
            }
            code = temp.deleteAll();
        }
        return code > 0 ? rMsg.netMSG(0, "模版方案删除成功") : result;
    }

    /**
     * 分页显示模版方案
     * 
     * @param idx
     * @param pageSize
     * @return
     */
    public String TempListPage(int idx, int pageSize) {
        return TempListPageBy(idx, pageSize, null);
    }

    public String TempListPageBy(int idx, int pageSize, String tempinfo) {
        long total = 0;
        if (!StringHelper.InvaildString(tempinfo)) {
            JSONArray CondArray = model.buildCond(tempinfo);
            if (CondArray != null && CondArray.size() > 0) {
                temp.where(CondArray);
            } else {
                return rMsg.netPAGE(idx, pageSize, total, new JSONArray());
            }
        }
        JSONArray array = temp.dirty().page(idx, pageSize);
        total = temp.count();
        return rMsg.netPAGE(idx, pageSize, total, (array != null && array.size() > 0) ? array : new JSONArray());
    }

}
