package i.am.eipeks.ei_flash;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class Flashlight extends AppCompatActivity {

    private ImageButton flashlight;
    private boolean isTorchOn, hasFlash;
    private Camera camera;
    Parameters parameters;

    RelativeLayout layout;

//    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        flashlight = (ImageButton) findViewById(R.id.flashlight);
        layout = (RelativeLayout) findViewById(R.id.activity_flashlight);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(!hasFlash){
            AlertDialog dialog =
                    new AlertDialog.Builder(this).create();
            dialog.setTitle("ERROR!!!");
            dialog.setMessage("Oops!!! Your device doesn't support flashlight");
            dialog.setButton(
                    DialogInterface.BUTTON_POSITIVE, "QUIT",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    }
            );
            dialog.show();
        } else {
            this.camera = Camera.open(0);
            parameters = this.camera.getParameters();
        }
        toggleBackground();
        toggleImageButton();
//        Toast.makeText(this, camera.getParameters().toString(), Toast.LENGTH_SHORT).show();

        flashlight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isTorchOn){
                            turnOffTorchlight();
                            isTorchOn = false;
                        } else{
                            turnOnTorchlight();
                            isTorchOn = true;
                        }
                    }
                }
        );
    }

    private void getCamera(){
        if(camera == null){
            try{
                this.camera = Camera.open();
                parameters = this.camera.getParameters();
//                Toast.makeText(this, camera.toString(), Toast.LENGTH_SHORT).show();
            }catch(RuntimeException e){
                Toast.makeText(this, "Couldn't get camera. Error: "
                        + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void turnOnTorchlight(){
//        if(camera == null || parameters == null){
//            return;
//        }
        if (this.camera != null) {
            parameters = camera.getParameters();
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            isTorchOn = true;
//            Toast.makeText(this, "Torch is on", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, String.valueOf(camera == null), Toast.LENGTH_SHORT).show();
            toggleBackground();
            toggleImageButton();
        }

    }

    private void turnOffTorchlight(){
//        if(camera == null || parameters == null){
//            return;
//        }
        parameters = camera.getParameters();
        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.stopPreview();
        isTorchOn = false;
//
//        Toast.makeText(this, "Torch is off", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, camera.toString(), Toast.LENGTH_SHORT).show();

        toggleBackground();
        toggleImageButton();
    }

    private void toggleImageButton(){
        if(isTorchOn){
            flashlight.setImageResource(R.drawable.torch_on);
        } else {
            flashlight.setImageResource(R.drawable.torch_off);
        }
    }

    private void toggleBackground(){
        if(isTorchOn){
            layout.setBackgroundColor(getResources().getColor(R.color.dark_background));
        } else{
            layout.setBackgroundColor(getResources().getColor(R.color.white_background));
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(this.camera != null){
            this.camera.release();
            this.camera = null;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(hasFlash){
            turnOnTorchlight();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(isTorchOn){
            turnOffTorchlight();
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        getCamera();
    }
}