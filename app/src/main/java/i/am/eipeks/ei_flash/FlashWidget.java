package i.am.eipeks.ei_flash;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.widget.RemoteViews;


public class FlashWidget extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == null){
            context.startService(
                    new Intent(context, ToggleService.class)
            );
        } else{
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        context.startService(
                new Intent(context, ToggleService.class)
        );
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public static class ToggleService extends IntentService{

        private Camera camera;
        Camera.Parameters parameters;

        public ToggleService() {
            super(ToggleService.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ComponentName componentName =
                    new ComponentName(this, FlashWidget.class);
            AppWidgetManager widgetManager =
                    AppWidgetManager.getInstance(this);
            widgetManager.updateAppWidget(componentName, myUpdate(this));
        }

        private RemoteViews myUpdate(Context context){
            RemoteViews viewToUpdate =
                    new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            if(camera == null){
                camera = Camera.open();
                parameters = camera.getParameters();
            }
            if(parameters.getFlashMode() ==
                    Camera.Parameters.FLASH_MODE_TORCH){
                viewToUpdate.setImageViewResource(R.id.widget_button
                , R.drawable.button_off);
                viewToUpdate.setImageViewResource(
                        R.id.widget_layout_background,
                        R.color.white_background
                );
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
            } else{
                viewToUpdate.setImageViewResource(R.id.widget_button
                , R.drawable.button_on);
                viewToUpdate.setImageViewResource(
                        R.id.widget_layout_background,
                        R.color.dark_background
                );
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }

            Intent new_intent = new Intent(this, FlashWidget.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, 0, new_intent, 0
            );
            viewToUpdate.setOnClickPendingIntent(
                    R.id.widget_button, pendingIntent
            );
            return viewToUpdate;
        }
    }
}
