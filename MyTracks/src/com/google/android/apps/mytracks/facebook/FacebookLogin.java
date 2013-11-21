package com.google.android.apps.mytracks.facebook;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

public class FacebookLogin extends Activity {

  private static final String PERMISSION = "publish_actions";


  //private Button postStatusUpdateButton;
  private Button postPhotoButton;

  private PendingAction pendingAction = PendingAction.NONE;


  private enum PendingAction {
    NONE,
    POST_PHOTO,
    POST_STATUS_UPDATE
  }



  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.facebook_login);

    // start Facebook Login
    Session.openActiveSession(this, true, new Session.StatusCallback() {

      // callback when session changes state
      @SuppressWarnings("deprecation")
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {

          // make request to the /me API
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
                TextView welcome = (TextView) findViewById(R.id.facebook_login_text);
                welcome.setText("Hello " + user.getName() + "!");
              }
            }
          });
        }
      }
    });

    onClickPostPhoto();

//    postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
//    postPhotoButton.setOnClickListener(new View.OnClickListener() {
//      public void onClick(View view) {
//        onClickPostPhoto();
//      }
//    });
    
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }


  private boolean hasPublishPermission() {
    Session session = Session.getActiveSession();
    return session != null && session.getPermissions().contains("publish_actions");
  }

  private void performPublish(PendingAction action, boolean allowNoSession) {
    Session session = Session.getActiveSession();
    if (session != null) {
      pendingAction = action;
      if (hasPublishPermission()) {
        // We can do the action right away.
        handlePendingAction();
        return;
      } else if (session.isOpened()) {
        // We need to get new permissions, then complete the action when we get called back.
        session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
        return;
      }
    }

    if (allowNoSession) {
      pendingAction = action;
      handlePendingAction();
    }
  }
  private void onClickPostPhoto() {
    performPublish(PendingAction.POST_PHOTO, false);
  }

  private void postPhoto() {
    if (hasPublishPermission()) {
      //TODO: get bitmap in other way
      
      String mytracksimg = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mytracks_chart.jpg";
      Bitmap image = BitmapFactory.decodeFile(mytracksimg);
      System.out.println("Path: " + mytracksimg);
       
      //Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.com_facebook_logo);
      Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
        @Override
        public void onCompleted(Response response) {
          showPublishResult(getString(R.string.photo_post), response.getGraphObject(), response.getError());
        }
      });
      request.executeAsync();
    } else {
      pendingAction = PendingAction.POST_PHOTO;
    }
  }

  private interface GraphObjectWithId extends GraphObject {
    String getId();
  }

  private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
    String title = null;
    String alertMessage = null;
    if (error == null) {
      title = getString(R.string.success);
      String id = result.cast(GraphObjectWithId.class).getId();
      alertMessage = getString(R.string.successfully_posted_post, message, id);
    } else {
      title = getString(R.string.error);
      alertMessage = error.getErrorMessage();
    }

    new AlertDialog.Builder(this)
    .setTitle(title)
    .setMessage(alertMessage)
    .setPositiveButton(R.string.ok, null)
    .show();
  }

  @SuppressWarnings("incomplete-switch")
  private void handlePendingAction() {
    PendingAction previouslyPendingAction = pendingAction;
    // These actions may re-set pendingAction if they are still pending, but we assume they
    // will succeed.
    pendingAction = PendingAction.NONE;

    switch (previouslyPendingAction) {
      case POST_PHOTO:
        postPhoto();
        break;
        //TODO: if we want to add this feature later
        //        case POST_STATUS_UPDATE:
        //            postStatusUpdate();
        //            break;
    }
  }
}

