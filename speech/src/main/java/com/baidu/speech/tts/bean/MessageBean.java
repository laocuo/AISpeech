package com.baidu.speech.tts.bean;

import android.text.TextUtils;

public class MessageBean {

    public static final int MIN = 9;

    public long id;

    public String text;

    public int level;

    public MessageBean() {
        setData(null);
    }

    public MessageBean(long id, int level, String text) {
        this.id = id;
        this.level = level;
        this.text = text;
    }

    public boolean isEmpty() {
        if (id > 0 && level == MIN && TextUtils.isEmpty(text)) {
            return true;
        } else {
            return false;
        }
    }

    public void setData(MessageBean bean) {
        if (bean != null) {
            this.id = bean.id;
            this.level = bean.level;
            this.text = bean.text;
        } else {
            this.id = 0;
            this.level = MIN;
            this.text = "";
        }
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", level=" + level +
                '}';
    }
}
