// -*- mode: c++ -*-
// Jordi Bataller i Mascarell

#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

#include "ServicioEnEmisora.h"

class EmisoraBLE {
private:
  const char * nombreEmisora;
  const uint16_t fabricanteID;
  const int8_t txPower;

public:
  using CallbackConexionEstablecida = void ( uint16_t connHandle );
  using CallbackConexionTerminada   = void ( uint16_t connHandle, uint8_t reason);

  EmisoraBLE( const char * nombreEmisora_, const uint16_t fabricanteID_,
              const int8_t txPower_ ) 
    :
    nombreEmisora( nombreEmisora_ ),
    fabricanteID( fabricanteID_ ),
    txPower( txPower_ )
  {
  } // ()

  // ............................................................
  // Encender emisora con refuerzo de nombre visible
  // ............................................................
  void encenderEmisora() {
    Bluefruit.begin();

    // Limpiar anuncios previos
    Bluefruit.Advertising.stop();
    Bluefruit.Advertising.clearData();
    Bluefruit.ScanResponse.clearData();

    // Configurar nombre visible
    Bluefruit.setName(nombreEmisora);

    // Forzar nombre en ambos paquetes (Advertising + ScanResponse)
    Bluefruit.Advertising.addName();  
    Bluefruit.ScanResponse.addName();

    // Añadir flags y potencia al paquete ADV
    Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
    Bluefruit.Advertising.addTxPower();

    detenerAnuncio();
  } // ()

  void encenderEmisora( CallbackConexionEstablecida cbce,
                        CallbackConexionTerminada cbct ) {
    encenderEmisora();
    instalarCallbackConexionEstablecida( cbce );
    instalarCallbackConexionTerminada( cbct );
  } // ()

  void detenerAnuncio() {
    if (estaAnunciando()) {
      Bluefruit.Advertising.stop(); 
    }
  }  

  bool estaAnunciando() {
    return Bluefruit.Advertising.isRunning();
  } 

  void emitirAnuncioIBeacon( uint8_t * beaconUUID, int16_t major, int16_t minor, uint8_t rssi ) {
    detenerAnuncio();
    
    BLEBeacon elBeacon( beaconUUID, major, minor, rssi );
    elBeacon.setManufacturer( fabricanteID );

    Bluefruit.setTxPower( txPower );
    Bluefruit.setName(nombreEmisora);

    // Forzar nombre en Advertising y ScanResponse
    Bluefruit.Advertising.addName();  
    Bluefruit.ScanResponse.addName();

    Bluefruit.Advertising.setBeacon( elBeacon );
    Bluefruit.Advertising.restartOnDisconnect(true);
    Bluefruit.Advertising.setInterval(100, 100);

    Bluefruit.Advertising.start(0); 
  } 

  void emitirAnuncioIBeaconLibre( const char * carga, const uint8_t tamanyoCarga ) {
    detenerAnuncio(); 

    Bluefruit.Advertising.clearData();
    Bluefruit.ScanResponse.clearData();

    Bluefruit.setName(nombreEmisora);

    // Forzar nombre en Advertising y ScanResponse
    Bluefruit.Advertising.addName();  
    Bluefruit.ScanResponse.addName();

    Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

    uint8_t restoPrefijoYCarga[4+21] = {
      0x4c, 0x00,
      0x02,
      21,
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-', '-', '-', '-', 
      '-'
    };

    memcpy( &restoPrefijoYCarga[4], &carga[0], ( tamanyoCarga > 21 ? 21 : tamanyoCarga ) ); 

    Bluefruit.Advertising.addData( BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
                                   &restoPrefijoYCarga[0],
                                   4+21 );

    Bluefruit.Advertising.restartOnDisconnect(true);
    Bluefruit.Advertising.setInterval(100, 100);    
    Bluefruit.Advertising.setFastTimeout(1);

    Bluefruit.Advertising.start(0); 
    Globales::elPuerto.escribir("emitiriBeacon libre  Bluefruit.Advertising.start(0);\n");
  } 

  bool anyadirServicio( ServicioEnEmisora & servicio ) {
    Globales::elPuerto.escribir(" Bluefruit.Advertising.addService( servicio ); \n");
    bool r = Bluefruit.Advertising.addService(servicio);
    if (!r) {
      Serial.println(" SERVICIO NO AÑADIDO \n");
    }
    return r;
  } 

  bool anyadirServicioConSusCaracteristicas( ServicioEnEmisora & servicio ) { 
    return (*this).anyadirServicio(servicio);
  }  

  template <typename ... T>
  bool anyadirServicioConSusCaracteristicas( ServicioEnEmisora & servicio,
                                             ServicioEnEmisora::Caracteristica & caracteristica,
                                             T& ... restoCaracteristicas) {
    servicio.anyadirCaracteristica(caracteristica);
    return anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);
  } 

  template <typename ... T>
  bool anyadirServicioConSusCaracteristicasYActivar( ServicioEnEmisora & servicio,
                                                     T& ... restoCaracteristicas) {
    bool r = anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);
    servicio.activarServicio();
    return r;
  } 

  void instalarCallbackConexionEstablecida( CallbackConexionEstablecida cb ) {
    Bluefruit.Periph.setConnectCallback(cb);
  } 

  void instalarCallbackConexionTerminada( CallbackConexionTerminada cb ) {
    Bluefruit.Periph.setDisconnectCallback(cb);
  } 

  BLEConnection * getConexion( uint16_t connHandle ) {
    return Bluefruit.Connection(connHandle);
  } 

}; // class

#endif
