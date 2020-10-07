package com.example.gezikitabim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.BitSet;


public class MainActivity extends AppCompatActivity {
    public static BitSet locations;
    public static BitSet names;
    public static ArrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;

    EditText mailtext;
    EditText sifretext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mailtext=findViewById(R.id.mailtext);
        sifretext=findViewById(R.id.sifretext);
        FirebaseUser user=mAuth.getCurrentUser();
        if (user !=null)
        {
            Intent ıntent=new Intent(getApplicationContext(),Konum_Ekle.class);
            startActivity(ıntent);
        }
    }
    public void giris(View view)
    {
        mAuth.signInWithEmailAndPassword(mailtext.getText().toString(),sifretext.getText().toString()).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Intent ıntent=new Intent(getApplicationContext(),Konum_Ekle.class);
                            startActivity(ıntent);
                        }

                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void kayit(View view)
    {
     mAuth.createUserWithEmailAndPassword(mailtext.getText().toString(),sifretext.getText().toString()).
             addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()) {
                         Toast.makeText(MainActivity.this, "Kullanıcı Basarıyla Olusturuldu!", Toast.LENGTH_SHORT).show();
                         Intent ıntent=new Intent(getApplicationContext(),Konum_Ekle.class);
                         startActivity(ıntent);
                     }

                 }
             }).addOnFailureListener(this, new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

         }
     });
    }

}
