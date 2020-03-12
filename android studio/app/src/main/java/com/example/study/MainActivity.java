package com.example.study;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.DefaultBlocks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;


public class MainActivity extends AbstractBlocklyActivity {

    Button btnClear, btnSave, btnLoad;
    private ImageView btnBack;
    private ImageView btnRun;
    private WebView mWebview;
    private MoveInterface moveInterface;
    private RadioGroup group;
    private JsonControl jsonControl;
    private ToolboxControl toolboxControl;

    protected View onCreateContentView(int parentId) {
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//隐藏Google自带的选择框

        View root = getLayoutInflater().inflate(R.layout.activity_main, null);
        btnBack = root.findViewById(R.id.btnBack);
        btnRun = root.findViewById(R.id.btnRun);
        btnClear = root.findViewById(R.id.btnClear);
        btnLoad = root.findViewById(R.id.btnLoad);
        btnSave = root.findViewById(R.id.btnSave);
        MListener mListener = new MListener();
        btnClear.setOnClickListener(mListener);
        btnLoad.setOnClickListener(mListener);
        btnSave.setOnClickListener(mListener);
        btnBack.setOnClickListener(mListener);
        btnRun.setOnClickListener(mListener);
        

        mWebview = (WebView) root.findViewById(R.id.webView);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebChromeClient(new WebChromeClient());//使用webView而不是外部浏览器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebview.loadUrl("file:///android_asset/empty.html");
        moveInterface = new MoveInterface(this);
        mWebview.addJavascriptInterface(moveInterface, "move");//JavaScript调用Java代码
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {//网页弹出提示框时触发此方法
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                //返回true表示不弹出系统的提示框，返回false表示弹出
                return true;
            }

        });

        jsonControl = new JsonControl();
        toolboxControl = new ToolboxControl();
        return root;
    }

    private class MListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClear:
                    onClearWorkspace();
                    break;
                case R.id.btnSave:
                    onSaveWorkspace();
                    break;
                case R.id.btnLoad:
                    onLoadWorkspace();
                    break;
                case R.id.btnRun:
                    if (getController().getWorkspace().hasBlocks()) {
                        onRunCode();
                        //todo:解析XML文件部分暂不开源，大家可以根据教程提供的思路进行尝试
//                        readXml();//自己解析xml文件
                    }
                    break;
                case R.id.btnBack:
//                    finish();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.textdialog, null);
                    final EditText text = view.findViewById(R.id.textDialog);
                    alert.setTitle("新建blockly块").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = text.getText().toString();
                            //注意下面操作的顺序
                            File fileBlockly = new File(MainActivity.this.getFilesDir(), "fileblockly.json");
                            jsonControl.addJson(fileBlockly,name);
                            File fileToolbox = new File(MainActivity.this.getFilesDir(), "filetoolbox.xml");
                            toolboxControl.addBlock(fileToolbox,jsonControl.getTypeByName(fileBlockly,name));
                            resetBlockFactory();
                            reloadToolbox();
                        }
                    }).setView(view).show();
                    break;
                default:
                    break;
            }
        }
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "mytoolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        final List<String> myBlockly = Arrays.asList(//自定义和默认的模块
                DefaultBlocks.COLOR_BLOCKS_PATH,
                DefaultBlocks.LOGIC_BLOCKS_PATH,
                DefaultBlocks.LOOP_BLOCKS_PATH,
                DefaultBlocks.MATH_BLOCKS_PATH,
                DefaultBlocks.TEXT_BLOCKS_PATH,
                DefaultBlocks.VARIABLE_BLOCKS_PATH,
                "myblockly.json"
        );
        return myBlockly;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        final List<String> myJs = Arrays.asList(//定义了js的函数
                "myjavascript.js");
        return myJs;
    }


    @NonNull
    protected CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback = new CodeGenerationRequest.CodeGeneratorCallback() {
        @Override
        public void onFinishCodeGeneration(String generatedCode) {//得到的代码自己处理
            Log.d("Code", "得到的代码:\n" + generatedCode);
            final String code = generatedCode;
            mWebview.post(new Runnable() {
                @Override
                public void run() {
                    String encoded = "execute("
                            + JavascriptUtil.makeJsString(code) + ")";
                    mWebview.loadUrl("javascript:" + encoded);
                    Log.d("Code", "执行的代码:\n" + encoded);
                }
            });
        }
    };

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    protected void onInitBlankWorkspace() {//加载空的工作区
        super.onInitBlankWorkspace();
    }


    @NonNull
    @Override
    protected String getWorkspaceSavePath() {
        return "savefile.xml";
    }

    @NonNull
    @Override
    protected String getWorkspaceAutosavePath() {
        return "autosavefile.xml";
    }

    @Override
    public void onLoadWorkspace() {//加载工作区
        super.onLoadWorkspace();
    }

    @Override
    public void onSaveWorkspace() {//保存工作区
        super.onSaveWorkspace();
    }
}






