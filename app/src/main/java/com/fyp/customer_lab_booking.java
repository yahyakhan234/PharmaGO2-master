package com.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.classes.users_collection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.fyp.lab_order_in_progress.LAB_COMPLETE_REQUESTED_KEY;
import static com.fyp.lab_price_order.PID_KEY;

public class customer_lab_booking extends AppCompatActivity {
    public static final String HAS_TEST_BOOKED_KEY="has_test_booked";
    public static final String IS_TEST_ACCEPTED_KEY="is_test_accepted";
    public static final String LAB_ACCEPTED_BOOKINGS_KEY="lab_accepted_bookings";
    public static final String TAG="Accept order";
    public static final String LAB_UNACCEPTED_BOOKINGS_KEY="lab_unaccepted_bookings";


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    TextView timeRequestedTV,dateRequestedTV,totalTV,testName,resultTV;
    MaterialButton liveChatButton,cancel_button_disabled,cancel_button,accept_button,lab_location_button,download_result,complete_order_button;
    String orderID,s,PID,pemail,lati,longi;
    ProgressDialog wait;
    DocumentReference fromPath,toPath;
    StorageReference storageReference;
    LinearLayout complete_order_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_lab_booking);
        wait=ProgressDialog.show(customer_lab_booking.this,"Processing","Please Wait");
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        timeRequestedTV=findViewById(R.id.time_requested);
        testName=findViewById(R.id.test_type);
        dateRequestedTV=findViewById(R.id.date_requested);
        totalTV=findViewById(R.id.total);
        liveChatButton=findViewById(R.id.live_chat);
        cancel_button=findViewById(R.id.cancel_button);
        cancel_button_disabled=findViewById(R.id.cancel_button_disabled);
        accept_button=findViewById(R.id.accept_button);
        resultTV=findViewById(R.id.result_in_days);
        complete_order_view=findViewById(R.id.complete_order_view);
        complete_order_button=findViewById(R.id.complete_order);
        lab_location_button=findViewById(R.id.lab_location);

        complete_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOrder();
            }
        });
        setDetails();
        download_result=findViewById(R.id.download_result);
        download_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder();
                cancel_button.setVisibility(View.GONE);
                cancel_button_disabled.setVisibility(View.VISIBLE);
                accept_button.setVisibility(View.GONE);
                liveChatButton.setVisibility(View.VISIBLE);
            }});

    }

    private void completeOrder() {
        new AlertDialog.Builder(customer_lab_booking.this)
                .setIcon(R.drawable.logo_splash)
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog wait = ProgressDialog.show(customer_lab_booking.this, "Processing", "Marking Your order is complete,Please wait", true);

                        db=FirebaseFirestore.getInstance();
                        s=FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        fromPath=db.collection("lab_accepted_bookings").document(s);
                        fromPath.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                orderID=documentSnapshot.getString("orderID");
                                pemail=documentSnapshot.getString("pemail");
                                toPath=db.collection("lab_tests_completed").document(orderID);
                                PID=documentSnapshot.getString("PID");
                                fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            final DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                toPath.set(document.getData())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                fromPath.delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                                db.collection("lab_bookings").document(PID)
                                                                                        .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        Map<String,Object> copyMap=new HashMap<>();
                                                                                        int copyCount=1;
                                                                                        if(!documentSnapshot.getString("count").equalsIgnoreCase("0")) {
                                                                                            for (int i = 1; i <= Integer.parseInt(documentSnapshot.getString("count")); i++) {
                                                                                                if (documentSnapshot.getString("order" + i).equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                                                                                    continue;
                                                                                                }
                                                                                                copyMap.put("order" + copyCount, documentSnapshot.getString("order" + i));
                                                                                                copyMap.put("name" + copyCount, documentSnapshot.getString("order" + i));
                                                                                                copyCount++;
                                                                                            }
                                                                                            int i=Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("count")));
                                                                                            i--;
                                                                                            copyMap.put("count",Integer.toString(i));
                                                                                            db.collection("lab_bookings").document(PID).set(copyMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    db.collection("lab_orders_completed")
                                                                                                            .document(pemail).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                            String completedCount=documentSnapshot.getString("count");
                                                                                                            assert completedCount != null;
                                                                                                            int cCount=Integer.parseInt(completedCount);
                                                                                                            cCount++;
                                                                                                            Map<String,Object> cOrdMap=new HashMap<>();
                                                                                                            cOrdMap.put("count",Integer.toString(cCount));
                                                                                                            cOrdMap.put("id"+cCount,orderID);
                                                                                                            db.collection("lab_orders_completed").document(pemail).set(cOrdMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                    Map<String,Object> m1=new HashMap<>();
                                                                                                                    m1.put(IS_TEST_ACCEPTED_KEY,false);
                                                                                                                    m1.put(HAS_TEST_BOOKED_KEY,false);
                                                                                                                    m1.put(LAB_COMPLETE_REQUESTED_KEY,false);
                                                                                                                    db.collection("users")
                                                                                                                            .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                                                                                                                            .set(m1,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void aVoid) {

                                                                                                                            Map<String, Object> ratingMap=new HashMap<>();
                                                                                                                            ratingMap.put("lab_rated",false);
                                                                                                                            ratingMap.put("user_rated",false);
                                                                                                                            db.collection("lab_tests_completed").document(orderID).set(ratingMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                                    db.collection("user_lab_orders_completed").document(mAuth.getCurrentUser().getUid()).get(Source.SERVER)
                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                                                @Override
                                                                                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                                                    int order_count= Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("count")));
                                                                                                                                                    order_count++;
                                                                                                                                                    Map<String,Object> usrOrdCompl=new HashMap<>();
                                                                                                                                                    usrOrdCompl.put("order"+order_count,orderID);
                                                                                                                                                    usrOrdCompl.put("count",""+order_count);
                                                                                                                                                    db.collection("user_lab_orders_completed").document(mAuth.getCurrentUser().getUid())
                                                                                                                                                            .set(usrOrdCompl,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                            wait.dismiss();
                                                                                                                                                            new AlertDialog.Builder(customer_lab_booking.this)
                                                                                                                                                                    .setTitle("Rate Pharmacist")
                                                                                                                                                                    .setMessage("Your order has been completed, Would you like to rate your experience? You can always give a rating later in the completed orders tab")
                                                                                                                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                                                                            finish();
                                                                                                                                                                            startActivity(new Intent(customer_lab_booking.this,order_completed_lab.class).putExtra("orderID",orderID));
                                                                                                                                                                        }
                                                                                                                                                                    })
                                                                                                                                                                    .setNegativeButton("Maybe Later", new DialogInterface.OnClickListener() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                                                                            finish();
                                                                                                                                                                            startActivity(new Intent(customer_lab_booking.this,dashboard.class));
                                                                                                                                                                        }
                                                                                                                                                                    })
                                                                                                                                                                    .setIcon(R.drawable.logo_splash)
                                                                                                                                                                    .show();

                                                                                                                                                        }
                                                                                                                                                    });

                                                                                                                                                }
                                                                                                                                            });

                                                                                                                                }
                                                                                                                            });
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }}});
                                                                                new GenerateNotif().orderCompleted(PID);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error deleting document", e);
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document", e);
                                                            }
                                                        });
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });


                            }
                        });


                        // startActivity(new Intent(customer_order_processed.this, dashboard.class));
                    }
                })
                .setTitle("Mark Order As complete")
                .setMessage("Are you sure you want to mark your order as complete? You wont be able to undo this option")
                .show();
    }

    void acceptOrder(){

        new AlertDialog.Builder(customer_lab_booking.this)
                .setTitle("Accept Order")
                .setMessage("Are you sure you want to Accept this order? You will not be able to cancel the order once it is accepted")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        s = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        fromPath = db.collection(LAB_UNACCEPTED_BOOKINGS_KEY).document(s);
                        toPath = db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(s);

                        fromPath.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                PID = documentSnapshot.getString("PID");
                            }
                        });
                        fromPath.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        toPath.set(document.getData())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        fromPath.delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        final Map<String, Object> m = new HashMap<>();
                                                                        m.put(HAS_TEST_BOOKED_KEY, true);
                                                                        m.put(IS_TEST_ACCEPTED_KEY,true);
                                                                        db.collection("users")
                                                                                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                                                                                .set(m, SetOptions.merge());
                                                                        final Map<String, Object> map = new HashMap<>();
                                                                        db.collection("lab_bookings").document(PID)
                                                                                .get(Source.SERVER)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        String i = Integer.toString(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("count"))) + 1);
                                                                                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAIL", MODE_PRIVATE);
                                                                                        String FullName = sharedPreferences.getString("NAME", "");
                                                                                        map.put("count", i);
                                                                                        map.put("order" + i, mAuth.getCurrentUser().getEmail());
                                                                                        map.put("name" + i, FullName);
                                                                                        db.collection("lab_bookings").document(PID).set(map, SetOptions.merge());
                                                                                    }
                                                                                });
                                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                        new GenerateNotif().sendNotificationToSinglePharmacist(PID);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error deleting document", e);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.logo_splash)
                .show();
    }

    void setDetails(){

        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.getBoolean(IS_TEST_ACCEPTED_KEY)){
                            cancel_button.setVisibility(View.GONE);
                            cancel_button_disabled.setVisibility(View.VISIBLE);
                            accept_button.setVisibility(View.GONE);
                            liveChatButton.setVisibility(View.VISIBLE);
                            db.collection(LAB_ACCEPTED_BOOKINGS_KEY).document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            PID=documentSnapshot.getString(PID_KEY);
                                            db.collection("pharma_users_online").document(PID).get(Source.SERVER)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            lati=documentSnapshot.getString("lat");
                                                            longi=documentSnapshot.getString("long");
                                                            lab_location_button.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    String uri = "http://maps.google.com/maps?q=" + lati+ "," + longi ;
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    });

                                            testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                                            timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                                            dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                                            totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                                            resultTV.setText(documentSnapshot.getString(lab_price_order.DELIVERY_IN_DAYS_KEY));
                                            orderID=documentSnapshot.getString("orderID");
                                            liveChatButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(customer_lab_booking.this,live_chat.class)
                                                            .putExtra("id","LC"+orderID));
                                                }
                                            });
                                            wait.dismiss();
                                        }
                                    });
                        }
                        else {
                            cancel_button.setVisibility(View.VISIBLE);
                            cancel_button_disabled.setVisibility(View.GONE);
                            accept_button.setVisibility(View.VISIBLE);
                            liveChatButton.setVisibility(View.GONE);
                            db.collection(LAB_UNACCEPTED_BOOKINGS_KEY).document(mAuth.getCurrentUser().getEmail()).get(Source.SERVER)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            PID=documentSnapshot.getString(PID_KEY);
                                            db.collection("pharma_users_online").document(PID).get(Source.SERVER)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            lati=documentSnapshot.getString("lat");
                                                            longi=documentSnapshot.getString("long");
                                                            lab_location_button.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    String uri = "http://maps.google.com/maps?saddr=" + lati+ "," + longi ;
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    });

                                            testName.setText(documentSnapshot.getString(lab_price_order.TEST_TYPE_NAME_KEY));
                                            timeRequestedTV.setText(documentSnapshot.getString(lab_price_order.TIME_REQUESTED_KEY));
                                            dateRequestedTV.setText(documentSnapshot.getString(lab_price_order.DATE_REQUESTED_KEY));
                                            totalTV.setText(documentSnapshot.getString(lab_price_order.TOTAL_KEY));
                                            resultTV.setText(documentSnapshot.getString(lab_price_order.DELIVERY_IN_DAYS_KEY));
                                            orderID=documentSnapshot.getString("orderID");
                                            liveChatButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(customer_lab_booking.this,live_chat.class)
                                                            .putExtra("id","LC"+orderID));

                                                }
                                            });
                                            wait.dismiss();
                                        }
                                    });

                        }
                        if (documentSnapshot.getBoolean(LAB_COMPLETE_REQUESTED_KEY)){
                            complete_order_view.setVisibility(View.VISIBLE);
                        }
                        else {
                            complete_order_view.setVisibility(View.GONE);
                        }

                    }
                });
    }
    public void download()
    {

        storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("lab_results").child(orderID);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                Toast.makeText(customer_lab_booking.this,"Download Started",Toast.LENGTH_LONG).show();
                downloadFile(customer_lab_booking.this,orderID,".pdf",DIRECTORY_DOWNLOADS,url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });


    }
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {


        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }
}