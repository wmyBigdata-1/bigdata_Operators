package com.wmy.ct.analysis;

import com.wmy.ct.analysis.tool.AnalysisTextTool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 分析数据
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/13
 */
public class AnalysisData {
    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new AnalysisTextTool(), args);
        // int result = ToolRunner.run( new AnalysisBeanTool(), args );
    }
}