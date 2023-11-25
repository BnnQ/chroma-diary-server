package me.bnnq.chromadiary.Configuration;

public class Environment
{
    public static String getFirebaseConfigPath()
    {
        return "src/main/resources/firebaseConfig.json";
    }

    public static String getFirebaseBucketName()
    {
        return "chroma-diary.appspot.com";
    }
}
