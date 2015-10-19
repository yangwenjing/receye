package com.iris.eyeiris.handlers;

import org.opencv.core.Mat;

/**
 * 虹膜图片的特征提取
 * Created by ywj on 15/10/19.
 */
public abstract class FeatureExtractor {
    private FeatureExtractor extractor;

    /**
     * 虹膜图片特征提取
     * @param src 输入的灰度图
     * @return 输出特征矩阵
     */
    public abstract Mat extract(Mat src);

    public FeatureExtractor getExtractor() {
        return this.extractor;
    }

    public void setExtractor(FeatureExtractor extractor) {
        this.extractor = extractor;
    }

}
