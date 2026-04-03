package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswardActivity extends AppCompatActivity {

    private EditText current_password, new_password, confirm_password;
    private Button update_password_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passward);

        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        update_password_button = findViewById(R.id.update_password_button);

        update_password_button.setOnClickListener(v -> {
            String nPass = new_password.getText().toString();
            String cPass = confirm_password.getText().toString();


            String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

            if (!nPass.equals(cPass)) {
                Toast.makeText(this, "كلمات المرور غير متطابقة", Toast.LENGTH_SHORT).show();
            } else if (!nPass.matches(pattern)) {
                Toast.makeText(this, "كلمة المرور ضعيفة! يجب أن تحتوي على 8 خانات، حروف كبيرة وصغيرة، أرقام ورموز", Toast.LENGTH_LONG).show();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(nPass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "تم تغيير كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "خطأ! يرجى إعادة تسجيل الدخول والمحاولة مرة أخرى", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
