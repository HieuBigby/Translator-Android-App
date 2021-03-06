package com.stillakid.translatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

//    private Spinner fromSpiner, toSpinner;
    private TextInputEditText sourceEdit;
    private ImageView micIV, swapIV;
    private MaterialButton translateBtn;
    private TextView translatedTV, fromTV, toTV;

//    String[] fromLanguages = {"From", "English", "Vietnamese"};
//    String[] toLanguages = {"To", "English", "Vietnamese"};

    private static final int REQUEST_PERMISSION_CODE = 1;
//    int languageCode, fromLanguageCode = 0, toLanguageCode = 0;
    int fromLanguageCode = FirebaseTranslateLanguage.EN;
    int toLanguageCode = FirebaseTranslateLanguage.VI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fromSpiner = findViewById(R.id.idFromSpinner);
//        toSpinner = findViewById(R.id.idToSpinner);
        sourceEdit = findViewById(R.id.idEditSource);
        micIV = findViewById(R.id.idIVMic);
        swapIV = findViewById(R.id.idSwapBtn);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);
        fromTV = findViewById(R.id.idFromTV);
        toTV = findViewById(R.id.idToTV);

//        fromSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                fromLanguageCode = getLanguageCode(fromLanguages[i]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);
//        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        fromSpiner.setAdapter(fromAdapter);
//        fromSpiner.setSelection(1);
//
//
//        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                toLanguageCode = getLanguageCode(toLanguages[i]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
//        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        toSpinner.setAdapter(toAdapter);
//        toSpinner.setSelection(2);


        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");
                if(sourceEdit.getText().toString().isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Vui l??ng nh???p t???/v??n b???n c???n d???ch", Toast.LENGTH_SHORT).show();
//                }else if(fromLanguageCode == 0){
//                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
//                }else if(toLanguageCode == 0){
//                    Toast.makeText(MainActivity.this, "Please select target language", Toast.LENGTH_SHORT).show();
                }else{
                    translateText(fromLanguageCode, toLanguageCode, sourceEdit.getText().toString());
                }
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");
                if(fromLanguageCode == FirebaseTranslateLanguage.EN){
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                }else{
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
                }
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "N??i v??o mic ????? chuy???n th??nh t???/v??n b???n");
                try{
                    startActivityForResult(i, REQUEST_PERMISSION_CODE);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        swapIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapLanguage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdit.setText(result.get(0));
            }
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source){
//        translatedTV.setText("Downloading model...");
        translatedTV.setText("??ang t???i d??? li???u...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                translatedTV.setText("Translating...");
                translatedTV.setText("??ang d???ch...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "Fail to translate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "X???y ra l???i khi d???ch", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "Fail to download language model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "X???y ra l???i khi t???i d??? li???u", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private int getLanguageCode(String language) {
//        int languageCode = 0;
//        switch (language){
//            case "English":
//                languageCode = FirebaseTranslateLanguage.EN;
//                break;
//            case "Vietnamese":
//                languageCode = FirebaseTranslateLanguage.VI;
//                break;
//            default:
//                languageCode = 0;
//                break;
//        }
//        return languageCode;
//    }

    private void swapLanguage(){
//        int fromSpinnerPosition = fromSpiner.getSelectedItemPosition();
//        int toSpinnerPosition = toSpinner.getSelectedItemPosition();
//
//        fromSpiner.setSelection(toSpinnerPosition);
//        toSpinner.setSelection(fromSpinnerPosition);

        String tempLanguage = fromTV.getText().toString();
        fromTV.setText(toTV.getText().toString());
        toTV.setText(tempLanguage);

        int tempLanguageCode = fromLanguageCode;
        fromLanguageCode = toLanguageCode;
        toLanguageCode = tempLanguageCode;
    }
}