package com.example.fracci.server;

import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoordsInterface {
    @GET("/test")
    //у Query пишем в кавычках то что в эклипсе Query ?firstName=Kat&lastName=Jojo
    public Call<LinkedList<Coords>> test(@Query("latt") String latt, @Query("lngg") String lngg);
    @GET("/coords/send")
    public Call<Void> sendCoords(@Query("latt") String latt, @Query("lngg") String lngg);
    @GET("/coords/get")
    public Call<ArrayList<Coords>> getCoords();


    //то как класс но только заголовок от метода без тела
}//ретрофит сам реализует без нас метод!!Аннотацие говорим ретрофиту что нужно метод допилить за нас
//каждую переменную тоже отметим чтоб правильно встала в поиск строку