package hipad.coapclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 官方节点测试
 */
public class OfficialAty extends AppCompatActivity {
    private TextView textContent, textCode, textName, textRtt;
    private Button btn_obs, btn_discover;
    private EditText editTextUrl;

    ProgressBar progressBar;
    NiceSpinner niceSpinner;
    /*存储节点名称*/
    List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_aty);

        textContent = (TextView) findViewById(R.id.textContent);
        btn_obs = (Button) findViewById(R.id.btn_obs);
        editTextUrl = (EditText) findViewById(R.id.edit_url);
        textCode = (TextView) findViewById(R.id.textCode);
        textName = (TextView) findViewById(R.id.textCodeName);
        textRtt = (TextView) findViewById(R.id.textRtt);
        btn_discover = (Button) findViewById(R.id.btn_discover);


//        editTextUrl.setText("coap://192.168.2.100:5683");


        /**
         * 这个控件存在点BUG，该处的循环添加的空数据，是为了扩展
         * 显示区域，我发现如果循环5次，以后更新数据也是显示5行区域，
         * 所以20次循环是扩展到页面以外*/
        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        list = new ArrayList<>();
        list.add("/obs");
        for (int i = 0; i < 20; i++) {
            list.add("");
        }
        niceSpinner.attachDataSource(list);


        progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        progressBar.setVisibility(View.GONE);
//        DoubleBounce doubleBounce = new DoubleBounce();
//        progressBar.setIndeterminateDrawable(doubleBounce);

    }


    public void clickGet(View view) {


        if (list == null)
            return;

        String uri = editTextUrl.getText().toString().trim();
        new CoapGetTask("get", getNode()).execute(uri);


    }

    public void clickPost(View view) {

        if (list == null)
            return;

        String uri = editTextUrl.getText().toString().trim();
        new CoapGetTask("post", getNode()).execute(uri);

    }

    public void clickPut(View view) {
        String uri = editTextUrl.getText().toString().trim();
        new CoapGetTask("put", getNode()).execute(uri);

    }


    public void clickDel(View view) {
        String uri = editTextUrl.getText().toString().trim();
        new CoapGetTask("del", getNode()).execute(uri);

    }


    public void clickDiscover(View view) {


        String uri = editTextUrl.getText().toString().trim();
        new CoapDiscoverTask().execute(uri);


    }

    public void clickObs(View view) {
        String uri = editTextUrl.getText().toString().trim();
        String text = btn_obs.getText().toString();


        if (TextUtils.equals(text, "Cancel")) {
            if (relation != null) {
                relation.reactiveCancel();
                btn_obs.setText("Observe");
            }
        } else {

            Obs(uri, getNode());
        }
    }


    CoapObserveRelation relation;

    private void Obs(String uri, String node) {


        CoapClient client = new CoapClient(uri + node);

        System.out.println("===============\nCO01+06");
        System.out.println("---------------\nGET /obs with Observe");
        relation = client.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = response;
                        handle.sendMessage(msg);

                    }

                    @Override
                    public void onError() {
                        System.err.println("-Failed--------");
                    }
                });
    }


    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                CoapResponse response = (CoapResponse) msg.obj;
                Toast.makeText(OfficialAty.this, response.getResponseText(), Toast.LENGTH_SHORT).show();
                btn_obs.setText("Cancel");
                if (response != null) {
                    textCode.setText(response.getCode().toString());
                    textName.setText(response.getCode().name());
                    textRtt.setText(response.advanced().getRTT() + " ms");
                    textContent.setText(response.getResponseText());
                } else {
                    textContent.setText("No response");
                }


            }

        }
    };


    class CoapGetTask extends AsyncTask<String, String, CoapResponse> {


        String type = "get";
        String node = "";

        public CoapGetTask(String type, String node) {

            this.type = type;
            this.node = node;
        }

        protected void onPreExecute() {
            // reset text fields
            textCode.setText("");
            textName.setText("Loading...");
            textRtt.setText("");
            textContent.setText("");
        }

        protected CoapResponse doInBackground(String... args) {

            CoapClient client = new CoapClient(args[0] + node);
            switch (type) {

                case "get":
                    return client.get();

                case "post":
                    return client.post("I am put,How are you!".getBytes(), MediaTypeRegistry.TEXT_PLAIN);

                case "put":

                    return client.put("I am put,How are you!".getBytes(), MediaTypeRegistry.TEXT_PLAIN);
                case "del":
                    return client.delete();
            }
            return client.get();


        }

        protected void onPostExecute(CoapResponse response) {
            if (response != null) {
                textCode.setText(response.getCode().toString());
                textName.setText(response.getCode().name());
                textRtt.setText(response.advanced().getRTT() + " ms");
                textContent.setText(response.getResponseText());
            } else {
                textName.setText("No response");
            }
        }
    }


    class CoapDiscoverTask extends AsyncTask<String, String, Set<WebLink>> {


        protected void onPreExecute() {
            // reset text fields
            progressBar.setVisibility(View.VISIBLE);

        }

        protected Set<WebLink> doInBackground(String... args) {
            CoapClient client = new CoapClient(args[0]);
            Set<WebLink> links = client.discover();
            return links;


        }

        protected void onPostExecute(Set<WebLink> links) {
            progressBar.setVisibility(View.GONE);

            list = new ArrayList<>();

            if (links != null) {
                for (WebLink o : links) {

                    StringBuilder sb = new StringBuilder();

                    sb.append(o.getURI());
                    //添加消息订阅标识
                    if (o.getAttributes().hasObservable()) {
                        sb.append("(消息订阅)");
                    }
                    list.add(sb.toString());
                }

                niceSpinner.attachDataSource(list);

            }


        }
    }


    //过滤标识字样--消息订阅


    public String getNode() {
        String node = list.get(niceSpinner.getSelectedIndex());
        String key = node.replace("(消息订阅)", "");
        return key;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (relation != null) {
            relation.reactiveCancel();
            btn_obs.setText("Observe");
        }
    }
}
