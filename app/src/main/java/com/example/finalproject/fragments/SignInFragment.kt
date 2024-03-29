package com.example.finalproject.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.AppActivity
import com.example.finalproject.CollectPDataActivity
import com.example.finalproject.R
import com.example.finalproject.utils.User
import com.example.finalproject.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var dataBaseRef: DatabaseReference

    private  companion object{
        private const val RC_SIGN_IN = 100
        private const val RC_SIGN_UP = 50
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.requireContext(),gso)

        checkUser()

        binding.googleBtn.setOnClickListener {
            Log.d(TAG,"onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.signinbtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                loginUser(email, pass)
                Toast.makeText(this.requireContext(),"loggedIn...\n$email", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }

    }
    private fun checkUser() {
        val firbaseUser = firebaseAuth.currentUser
        if (firbaseUser != null){
            dataBaseRef.child(firbaseUser.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The child node exists in the parent node
                        startActivity(Intent(requireContext(), AppActivity::class.java))
                        requireActivity().finish()

                    } else {
                        // The child node does not exist in the parent node
                        startActivity(Intent(requireContext(), CollectPDataActivity::class.java))
                        requireActivity().finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur
                    Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()

                }
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            Log.d(TAG,"onActivityResult: Google SignIn result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAcc(account)
            }
            catch (e: Exception){
                Log.d(TAG,"onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAcc(account: GoogleSignInAccount?) {
        Log.d(TAG,"firebaseAuthWithGoogleAcc: begin firebase auth with google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG,"firebaseAuthWithGoogleAcc: loggedIn")

                val firebaseuser = firebaseAuth.currentUser
                val uid = firebaseAuth.uid
                val email = firebaseuser!!.email
                Log.d(TAG,"firebaseAuthWithGoogleAcc: Uid: $uid")
                Log.d(TAG,"firebaseAuthWithGoogleAcc: Email: $email")

                if (authResult.additionalUserInfo!!.isNewUser){
                    Log.d(TAG,"firebaseAuthWithGoogleAcc: account created... \n$email")
                    Toast.makeText(this.requireContext(),"Account created...\n$email", Toast.LENGTH_SHORT).show()
                    val user = User(firebaseuser.displayName,firebaseuser.email.toString(),null,null)
                    dataBaseRef.child(firebaseuser.uid).setValue(user).addOnCompleteListener{
                        if (it.isSuccessful){
                            Toast.makeText(context,"user saved in database",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Log.d(TAG,"firebaseAuthWithGoogleAcc: Existing user... \n$email")
                    Toast.makeText(this.requireContext(),"loggedIn...\n$email", Toast.LENGTH_SHORT).show()

                }

                startActivity(Intent(requireActivity(),AppActivity::class.java))
                requireActivity().finish()

            }
            .addOnFailureListener{ e->
                Log.d(TAG,"firebaseAuthWithGoogleAcc: Loggin Failed due to ${e.message}")
                Toast.makeText(this.requireContext(),"Loggin Failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
    private fun loginUser(email: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(requireActivity(),AppActivity::class.java))
                requireActivity().finish()

            }

            else
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }
}
