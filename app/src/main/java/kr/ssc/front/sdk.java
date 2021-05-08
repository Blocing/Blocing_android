package kr.ssc.front;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiresApi(api = Build.VERSION_CODES.O)
public class sdk {
    public static void main(String[] args) throws IOException {

        Path walletDirectory = Paths.get("wallet");
        System.out.println(walletDirectory);

    }
}
