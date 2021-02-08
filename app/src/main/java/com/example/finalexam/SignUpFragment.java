package com.example.finalexam;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    EditText emailEditTextRegister,passwordEditTextRegister, nameRegisterET;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        getActivity().setTitle("New Account Screen");

        nameRegisterET = view.findViewById(R.id.nameRegisterET);
        emailEditTextRegister = view.findViewById(R.id.emailRegisterET);
        passwordEditTextRegister = view.findViewById(R.id.passwordRegisterET);

        view.findViewById(R.id.submitRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameRegisterET.getText().toString();
                final String email = emailEditTextRegister.getText().toString();
                final String password = passwordEditTextRegister.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                if (email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[a-zA-Z0-9]+\\.[a-zA-Z]+$")) {
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.email_hint));
                } else if (password.isEmpty() && (password.length() < 8)) {
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.password_hint));
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> map = new HashMap<>();
                                map.put("username",name);//name
                                db.collection("users").document(user.getUid()).set(map);
                                mListener.goToMainActivityAfterRegistering();
                            }else {
                                AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error),task.getException().getMessage());
                            }
                        }
                    });
                }

            }
        });

        view.findViewById(R.id.cancelNewForumButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToLoginFrag();
            }
        });

        return view;
    }



    IRegisterListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IRegisterListener) context;
    }

    public interface IRegisterListener{
        void goBackToLoginFrag();
        void goToMainActivityAfterRegistering();
    }
}