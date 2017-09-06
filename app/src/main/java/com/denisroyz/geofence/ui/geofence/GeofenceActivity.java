package com.denisroyz.geofence.ui.geofence;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.denisroyz.geofence.R;
import com.denisroyz.geofence.model.GPSRule;
import com.denisroyz.geofence.model.WifiRule;
import com.denisroyz.geofence.validation.GPSRuleObjectValidator;
import com.denisroyz.geofence.validation.ObjectValidatorError;
import com.denisroyz.geofence.validation.ObjectValidatorResult;

public class GeofenceActivity extends AppCompatActivity implements GeofenceView{

    GPSRuleObjectValidator gpsRuleObjectValidator;
    GeofencePresenter mGeofencePresenter;

    ToggleButton toggleButton;
    TextView statusTextView;

    Button saveConfigurationButton;

    EditText wifiNetworkNameRuleEditText;
    EditText gpsLatitudeRuleEditText;
    EditText gpsLongitudeRuleEditText;
    EditText gpsRadiusRuleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        initDependencies();
        bindViews();
        mGeofencePresenter.fillView();
    }


    private void initDependencies(){
        mGeofencePresenter = new GeofencePresenterImpl(this);
        gpsRuleObjectValidator = new GPSRuleObjectValidator();
    }

    private void bindViews(){
        wifiNetworkNameRuleEditText = findViewById(R.id.geofence_configuration_wifi_name_et);
        gpsLatitudeRuleEditText = findViewById(R.id.geofence_configuration_gps_lat);
        gpsLongitudeRuleEditText = findViewById(R.id.geofence_configuration_gps_lon);
        gpsRadiusRuleEditText = findViewById(R.id.geofence_configuration_gps_radius);
        saveConfigurationButton = findViewById(R.id.geofence_configuration_save_button);
        statusTextView = findViewById(R.id.status_text_view);
        toggleButton = findViewById(R.id.geofence_sensors_toggle);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleButtonClick();
            }
        });
        saveConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveConfigurationButtonClick();
            }
        });
    }

    private void onSaveConfigurationButtonClick(){
        ObjectValidatorResult<GPSRule> gpsRule = readGPSRule();
        if (gpsRule.isValid()){
            saveConfigurationButton.setEnabled(false);
        }
    }


    private ObjectValidatorResult<GPSRule> readGPSRule(){
        GPSRule gpsRule = new GPSRule();
        if (!gpsLatitudeRuleEditText.getText().toString().isEmpty()) {
            gpsRule.setLat( Double.parseDouble(gpsLatitudeRuleEditText.getText().toString()));
        }
        if (!gpsLongitudeRuleEditText.getText().toString().isEmpty()) {
            gpsRule.setLon( Double.parseDouble(gpsLongitudeRuleEditText.getText().toString()));
        }
        if (!gpsRadiusRuleEditText.getText().toString().isEmpty()) {
            gpsRule.setRadius( Double.parseDouble(gpsRadiusRuleEditText.getText().toString()));
        }
        ObjectValidatorResult<GPSRule> validatedGPSRule = gpsRuleObjectValidator.validateObject(this, gpsRule);
        processValidationError(gpsLatitudeRuleEditText, validatedGPSRule.getError(GPSRuleObjectValidator.FIELD_LAT));
        processValidationError(gpsLongitudeRuleEditText, validatedGPSRule.getError(GPSRuleObjectValidator.FIELD_LON));
        processValidationError(gpsRadiusRuleEditText, validatedGPSRule.getError(GPSRuleObjectValidator.FIELD_RADIUS));
        return validatedGPSRule;
    }

    private void processValidationError(EditText editText,@Nullable  ObjectValidatorError objectValidatorError){
        if (objectValidatorError==null){
            editText.setError(null);
        } else {
            editText.setError(objectValidatorError.getMessage());
        }
    }

    private void onToggleButtonClick(){
        mGeofencePresenter.enableSearch(toggleButton.isChecked());
    }

    @Override
    public void displayGeofenceStatus(boolean geoFenceStatus) {
        statusTextView.setText(geoFenceStatus?R.string.in_geofence_area:R.string.not_in_geofence_area);
    }

    @Override
    public void displayRulesPicker(GPSRule gpsRule, WifiRule wifiRule) {
        wifiNetworkNameRuleEditText.setText(wifiRule.getWifiNetworkName());
        gpsLatitudeRuleEditText.setText(String.valueOf(gpsRule.getLat()));
        gpsLongitudeRuleEditText.setText(String.valueOf(gpsRule.getLon()));
        gpsRadiusRuleEditText.setText(String.valueOf(gpsRule.getRadius()));
    }
}
