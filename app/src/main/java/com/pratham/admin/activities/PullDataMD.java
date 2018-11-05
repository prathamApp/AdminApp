package com.pratham.admin.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.CRLmd;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.ConnectionReceiver;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PI;
import static com.pratham.admin.util.APIs.RI;
import static com.pratham.admin.util.APIs.SC;

public class PullDataMD extends AppCompatActivity implements ConnectionReceiverListener {
    @BindView(R.id.rg_programs)
    RadioGroup radioGroupPrograms;

    @BindView(R.id.spinner_state)
    Spinner spinner_state;

   /* @BindView(R.id.spinner_block)
    MultiSpinner spinner_block;
*/

    Context context;
    boolean internetIsAvailable = false;

   /* List blocks;
    boolean[] selectedBlockItems;
    String selectedBlocks = "";*/

    //   boolean[] selectedItems;
    String[] states;
    String[] stateCodes;
    // String[] selectedStatesArray;
    List<String> selectedStateCodeList;
    //  String selectedStates = "";
    // private int stateIndexDownload = 0;
    Dialog dialog;

    //  String crlUrl;
    //  int programID;
    List<Village> allVillageList;
    List<CRLmd> allCrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_data_md);
        ButterKnife.bind(this);
        // Check Internet Connection
        checkConnection();
        allVillageList = new ArrayList();
        allCrlList = new ArrayList();
        context = PullDataMD.this;
        spinnerState();
        // spinnerBlock();

    }

    @Override
    protected void onResume() {
        super.onResume();
        radioGroupPrograms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                spinner_state.setSelection(0);
                allCrlList.clear();
            }
        });
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void spinnerState() {
        states = getResources().getStringArray(R.array.india_states);
        //   states = Arrays.copyOfRange(states, 1, states.length);
        stateCodes = getResources().getStringArray(R.array.india_states_shortcode);
        //   stateCodes = Arrays.copyOfRange(stateCodes, 1, stateCodes.length);

        selectedStateCodeList = new ArrayList<>();
        ArrayAdapter subAdapter = new ArrayAdapter(PullDataMD.this, android.R.layout.simple_spinner_dropdown_item, states);
        //  spinner_state.setAdapter(subAdapter, false, onSelectedListener);
        spinner_state.setAdapter(subAdapter);
        // set initial selection
        // selectedItems = new boolean[subAdapter.getCount()];
       /* spinner_state.setHint("Select States");
        spinner_state.setHintTextColor(Color.BLACK);*/
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = stateCodes[position];
                if (selectedState.equals("SELECT STATE")) {
                    //on Nothing selected
                    Toast.makeText(context, "Select a state ", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedRadioButtonId = radioGroupPrograms.getCheckedRadioButtonId();
                    if (selectedRadioButtonId == -1) {
                        Toast.makeText(context, "Please Select Program", Toast.LENGTH_SHORT).show();
                    } else {
                        RadioButton radioButton = findViewById(selectedRadioButtonId);
                        String selectedProgram = radioButton.getText().toString();
                  /*  stateIndexDownload = 0;
                    allVillageList.clear();*/
                        if (internetIsAvailable) {
                            showDialoginApiCalling();
                            switch (selectedProgram) {
                                case APIs.HL:
                                    pullCRL(APIs.HLpullCrlsURL + selectedState);
                                case APIs.UP:
                                    pullCRL(APIs.UPpullCrlsURL + selectedState);
                                    break;
                                case APIs.ECE:
                                    pullCRL(APIs.ECEpullCrlsURL + selectedState);
                                    break;
                                case RI:
                                    pullCRL(APIs.RIpullCrlsURL + selectedState);
                                    break;
                                case SC:
                                    //wrong API
                                    pullCRL(APIs.SCpullCrlsURL + selectedState);
                                    break;
                                case PI:
                                    pullCRL(APIs.PIpullCrlsURL + selectedState);
                                    break;
                            }
                        } else {
                            //no internet
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void pullCRL(final String url) {
        // stateIndexDownload++;
        if (internetIsAvailable) {
            allCrlList.clear();
            showDialoginApiCalling();
            AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    parseCRLJSon(response.toString());
                    //   pullCRL(url);
                    dismissShownDialog();
                    // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(ANError error) {
                    spinner_state.setSelection(0);
                    dismissShownDialog();
                    if (!internetIsAvailable) {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Load API Failed.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } else {
            Toast.makeText(context, " Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseCRLJSon(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<CRLmd>>() {
        }.getType();
        ArrayList<CRLmd> crl = gson.fromJson(json, listType);
        allCrlList.clear();
        allCrlList.addAll(crl);
    }


    @OnClick(R.id.btn_save)
    public void saveData() {
        AppDatabase.getDatabaseInstance(this).getCRLmd_dao().insertAllCRLmd(allCrlList);
        Toast.makeText(context, "Inserted" + allCrlList.size(), Toast.LENGTH_SHORT).show();
    }


    private void showDialoginApiCalling() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle("Pulling... ");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissShownDialog() {
        if (dialog != null) dialog.dismiss();
        dialog = null;
    }
    // Listener
 /*  private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedStates = "";
            //selectedStatesArray = new String[selected.length];
            selectedStateCodeList.clear();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    //  selectedStatesArray[i] = states[i];
                    selectedStates = selectedStates + "," + states[i];
                    //for state Codes
                    selectedStateCodeList.add(stateCodes[i]);
                }
            }
            selectedStates = selectedStates.replaceFirst(",", "");
            //pulling blocks first call
            int selectedRadioButtonId = radioGroupPrograms.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                Toast.makeText(context, "Please Select Program", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton radioButton = findViewById(selectedRadioButtonId);
                String selectedProgram = radioButton.getText().toString();
                stateIndexDownload = 0;
                allVillageList.clear();
                if (internetIsAvailable) {
                    showDialoginApiCalling();
                    switch (selectedProgram) {
                        case APIs.HL:
                            pullBlocksByStates(APIs.HLpullVillagesURL);
                            crlUrl = APIs.HLpullCrlsURL;

                            break;
                        case APIs.ECE:
                            pullBlocksByStates(APIs.ECEpullVillagesURL);
                            crlUrl = APIs.ECEpullCrlsURL;

                            break;
                        case RI:
                            pullBlocksByStates(APIs.RIpullVillagesURL);
                            crlUrl = APIs.RIpullCrlsURL;
                            break;
                        case SC:
                            pullBlocksByStates(APIs.SCpullVillagesURL);
                            crlUrl = APIs.SCpullCrlsURL;
                            break;
                        case PI:
                            pullBlocksByStates(APIs.PIpullVillagesURL);
                            crlUrl = APIs.PIpullCrlsURL;
                            break;
                    }
                } else {

                }

            }


            //todo  remove toast
            for (int k = 0; k < selectedStateCodeList.size(); k++)
                Toast.makeText(PullDataMD.this, "" + selectedStateCodeList.get(k).toString(), Toast.LENGTH_SHORT).show();
            Log.d("MD", "selectedStates :: " + selectedStates);

        }
    };
*/
   /* private void pullBlocksByStates(final String url) {
        if (selectedStateCodeList.size() > stateIndexDownload) {
            String urlTemp = url + selectedStateCodeList.get(stateIndexDownload).toString();
            stateIndexDownload++;
            if (internetIsAvailable) {
                AndroidNetworking.get(urlTemp).build().getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseVillageJSon(response.toString());
                        pullBlocksByStates(url);
                        // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        //todo spinner_state.setSelection(0);
                        if (!internetIsAvailable) {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Load API Failed.", Toast.LENGTH_LONG).show();
                        }
                        // Log.d("error", "" + error);
                   *//* dismissShownDialog();
                    apiLoadFlag = false;*//*
                    }
                });


            } else {
                Toast.makeText(context, " Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        } else { *//*After pulling all Villages*//*
            // spinnerBlock();
            allCrlList.clear();
            stateIndexDownload = 0;
            pullCRL(crlUrl);
        }
    }
*/

    /* private void parseVillageJSon(String json) {
         Gson gson = new Gson();
         Type listType = new TypeToken<ArrayList<Village>>() {
         }.getType();
         ArrayList<Village> village = gson.fromJson(json, listType);
         allVillageList.addAll(village);
     }
 */


    /* private void spinnerBlock() {
         LinkedHashSet hs = new LinkedHashSet();
         blocks = new ArrayList();
         for (int vId = 0; allVillageList.size() > vId; vId++) {
             if (selectedStateCodeList.contains(allVillageList.get(vId).getState())) {
                 blocks.add(allVillageList.get(vId).getBlock());
             }
         }
         hs.addAll(blocks);
         blocks.clear();
         blocks.addAll(hs);

         ArrayAdapter subAdapter = new ArrayAdapter(PullDataMD.this, android.R.layout.simple_spinner_dropdown_item, blocks);
         spinner_block.setAdapter(subAdapter, false, onSelectedBlockListener);
         // set initial selection
         selectedBlockItems = new boolean[subAdapter.getCount()];
         spinner_block.setHint("Select States");
         spinner_block.setHintTextColor(Color.BLACK);

     }

     private MultiSpinner.MultiSpinnerListener onSelectedBlockListener = new MultiSpinner.MultiSpinnerListener() {
         public void onItemsSelected(boolean[] selected) {
             // Do something here with the selected items
             selectedBlocks = "";
             //selectedStatesArray = new String[selected.length];
             for (int i = 0; i < selected.length; i++) {
                 if (selected[i]) {
                     //  selectedStatesArray[i] = states[i];
                     selectedBlocks = selectedBlocks + "," +blocks.get(i);
                 }
             }
             selectedBlocks = selectedBlocks.replaceFirst(",", "");
             Toast.makeText(context, ""+selectedBlocks, Toast.LENGTH_SHORT).show();
         }
     };
 */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }
}
