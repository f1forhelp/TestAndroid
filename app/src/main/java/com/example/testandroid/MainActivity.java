package com.example.testandroid;
import com.facebook.soloader.SoLoader;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.facebook.soloader.SoLoader;
import com.facebook.soloader.DirectorySoSource;
import com.facebook.soloader.ExternalSoMapping;
import com.facebook.soloader.NativeLibrary;
import com.facebook.soloader.SoLoader;
import com.unity3d.player.UnityPlayerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing so loader.
        try {
            SoLoader.deinitForTest();
            Thread.sleep(10);
            SoLoader.init(this, false);
//            SoLoader.deinitForTest();
//            SoLoader.init(this, ExternalSoMapping);
            String tempFilePath =  getFilesDir().getAbsolutePath();
            File customLibDir = new File(tempFilePath);
            DirectorySoSource customSoSource = new DirectorySoSource(customLibDir, DirectorySoSource.RESOLVE_DEPENDENCIES);
            SoLoader.prependSoSource(customSoSource);
        }catch (Exception e){
            Log.e("SOLOADERROR",e.getMessage());
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnUnity = findViewById(R.id.launchUnity);
        Button btnBakeUnity = findViewById(R.id.bakeUnity);
        Button btnShowLoadedLibrary =  findViewById(R.id.showLoadedLibrary);

        btnUnity.setOnClickListener(
                v -> {
                    Intent i = new Intent(MainActivity.this, UnityPlayerActivity.class);
                    startActivity(i);
                }
        );

        btnBakeUnity.setOnClickListener(
                v -> {
                   loadLibraries();

                }
        );

        btnShowLoadedLibrary.setOnClickListener(
                v -> {
                    logLoadedLibraries();
                }
        );
    }



    private void loadLibraries() {

        String[] soFilesToLoad = {
                "unity",           // Unity's base library
                "il2cpp",          // IL2CPP runtime (dependent on libunity.so)
                "lib_burst_generated", // Burst-compiled library
                "main",            // Main application library
                "AndroidCpuUsage"  // Standalone library

        };

        for (String soFileName : soFilesToLoad) {
            File libFile = new File(getFilesDir(), "lib"+soFileName+".so");

            if (libFile.exists()) {
                try {
                    SoLoader.loadLibrary(soFileName);
                    Toast.makeText(this,"Loaded :"+ soFileName,Toast.LENGTH_SHORT).show();
//                    System.load(libFile.getAbsolutePath());
                    Log.d("LibraryLoader", "Library " + soFileName + " loaded successfully!");
                } catch (UnsatisfiedLinkError e) {
                    Log.e("LibraryLoader", "Failed to load library " + soFileName + ": " + e.getMessage());
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("LibraryLoader", "Library " + soFileName + " not found in internal storage.");
                Toast.makeText(this, "Library " + soFileName + " not found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void logLoadedLibraries() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(".so")) {
                   Log.i("LoadedSoFiles","Files:"+line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}