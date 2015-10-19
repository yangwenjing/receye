package com.iris.eyeiris.handlers;

import org.opencv.core.Mat;

import java.util.logging.Handler;

/**
 * Created by ywj on 15/10/19.
 */
public abstract class LocateHandler {
    private LocateHandler handler;

    /**
     * 虹膜定位，找到虹膜，然后归一化为统一格式的
     * @param src 输入的灰度图
     * @return 输出的矩阵
     */
    public abstract Mat handleLocateIris(Mat src);

    public LocateHandler getHandler() {
        return this.handler;
    }

    public void setHandler(LocateHandler handler) {
        this.handler = handler;
    }

}
