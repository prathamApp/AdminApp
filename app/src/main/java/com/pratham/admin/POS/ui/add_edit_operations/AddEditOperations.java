package com.pratham.admin.POS.ui.add_edit_operations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditOperations extends AppCompatActivity {

    @BindView(R.id.defaultLayout)
    LinearLayout defaultLayout;
    @BindView(R.id.RILayout)
    LinearLayout RILayout;
    @BindView(R.id.DataAnalysisLayout)
    LinearLayout DataAnalysisLayout;
    @BindView(R.id.sp_ProgramID)
    Spinner sp_ProgramID;
    String program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_operations);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        // hide all views
        defaultLayout.setVisibility(View.GONE);
        RILayout.setVisibility(View.GONE);
        DataAnalysisLayout.setVisibility(View.GONE);
        populatePrograms();

    }

    private void populatePrograms() {
        ArrayAdapter ProgramAdapter = new ArrayAdapter(AddEditOperations.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Programs));
        sp_ProgramID.setAdapter(ProgramAdapter);

        sp_ProgramID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedProgram = sp_ProgramID.getSelectedItem().toString();
                if (selectedProgram.contains("Select")) {
                    // hide all views
                    defaultLayout.setVisibility(View.GONE);
                    RILayout.setVisibility(View.GONE);
                    DataAnalysisLayout.setVisibility(View.GONE);
                } else if (selectedProgram.equalsIgnoreCase("Others")) {
                    // Dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddEditOperations.this);
                    LayoutInflater inflater = AddEditOperations.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.others_dialog, null);
                    dialogBuilder.setView(dialogView);
                    final EditText edt = (EditText) dialogView.findViewById(R.id.edt_Others);
                    dialogBuilder.setCancelable(false);
                    edt.setHint("e.g. Read India");
                    dialogBuilder.setTitle("Please Mention Others ");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            program = edt.getText().toString().trim();
                            if (edt.getText().toString().trim().equalsIgnoreCase("")) {
                                populatePrograms();
                            } else {
                                Toast.makeText(AddEditOperations.this, "Program = " + program, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //pass
                            populatePrograms();
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                } else {
                    program = selectedProgram;
                    if (program.contains("Read")) {
                        defaultLayout.setVisibility(View.GONE);
                        DataAnalysisLayout.setVisibility(View.VISIBLE);
                        RILayout.setVisibility(View.VISIBLE);
                    } else {
                        defaultLayout.setVisibility(View.VISIBLE);
                        DataAnalysisLayout.setVisibility(View.VISIBLE);
                        RILayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void AddCrl(View view) {
    }

    public void AddGroup(View view) {
    }

    public void AddStudent(View view) {
    }

    public void EditStudent(View view) {
    }

    public void AddUnit(View view) {
    }

    public void SelectUnitToEdit(View view) {
    }
}
