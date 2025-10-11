package com.example.sprint0_biometria;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

// ------------------------------------------------------------------
// MainActivity - mínima corrección p/ BLE + compat. 9..12+
// ------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    // Unifica con el nombre que emite tu Arduino (EmisoraBLE.setName)
    private static final String NOMBRE_BEACON = "Santi";

    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;

    // deduplicación por contador (major bajo)
    private Integer ultimoContador = null;

    // --------------------------------------------------------------
    // Permisos según versión (Android 12+ usa BLUETOOTH_*; 6-11 usa LOCATION)
    private boolean tienePermisosBLE() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean scan = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED;
            boolean connect = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED;
            return scan && connect;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // <23 no hay runtime perms relevantes para BLE
    }

    private void solicitarPermisosSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            List<String> faltan = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) faltan.add(Manifest.permission.BLUETOOTH_SCAN);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) faltan.add(Manifest.permission.BLUETOOTH_CONNECT);
            if (!faltan.isEmpty()) {
                ActivityCompat.requestPermissions(this, faltan.toArray(new String[0]), CODIGO_PETICION_PERMISOS);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODIGO_PETICION_PERMISOS);
            }
        }
    }

    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);

                BluetoothDevice device = resultado.getDevice();
                String name = (device != null ? device.getName() : null);
                Log.d(ETIQUETA_LOG, "Visto: " + name + " RSSI=" + resultado.getRssi());

                // Procesa siempre; el parser de iBeacon validará si es nuestro
                procesarTramaBeacon(resultado);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(ETIQUETA_LOG, "Error en escaneo: " + errorCode);
            }
        };

        if (!tienePermisosBLE()) {
            Log.w(ETIQUETA_LOG, "Sin permisos BLE en esta versión. Solicitando...");
            solicitarPermisosSiHaceFalta();
            return;
        }

        if (this.elEscanner == null) {
            Log.e(ETIQUETA_LOG, "No hay escáner BLE (¿BT desactivado?).");
            return;
        }

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        this.elEscanner.startScan(null, settings, this.callbackDelEscaneo);
    }

    // --------------------------------------------------------------
    private void procesarTramaBeacon(ScanResult resultado) {
        try {
            byte[] scanRecord = resultado.getScanRecord().getBytes();

            // iBeacon layout típico: prefijo + 0x4C00 + 0x02 + 0x15 + UUID(16) + major(2) + minor(2) + txPower(1)
            int startIndex = 9; // tu offset original; mantenemos mínimo cambio

            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startIndex, uuidBytes, 0, 16);

            byte[] majorBytes = new byte[2];
            System.arraycopy(scanRecord, startIndex + 16, majorBytes, 0, 2);

            byte[] minorBytes = new byte[2];
            System.arraycopy(scanRecord, startIndex + 18, minorBytes, 0, 2);

            byte txPower = scanRecord[startIndex + 20];

            // Corrección endian (como ya tenías)
            int major = ((majorBytes[1] & 0xFF) << 8) | (majorBytes[0] & 0xFF);
            int idMedicion = major & 0xFF;
            int contador = (major >> 8) & 0xFF;

            int valorMedido = ((minorBytes[0] & 0xFF) << 8) | (minorBytes[1] & 0xFF);

            Log.d(ETIQUETA_LOG, "----------------------------------------------------");
            Log.d(ETIQUETA_LOG, "UUID: " + Utilidades.bytesToString(uuidBytes));
            Log.d(ETIQUETA_LOG, "Major: " + major + " → idMedicion=" + idMedicion + " contador=" + contador);
            Log.d(ETIQUETA_LOG, "Minor (valor medido): " + valorMedido);
            Log.d(ETIQUETA_LOG, "TxPower: " + txPower + "  RSSI: " + resultado.getRssi());
            Log.d(ETIQUETA_LOG, "----------------------------------------------------");

            // Asegurar que el contador sea distinto para que no se envíen medidas duplicadas a la bbdd (Próximos Sprints)
               //     if (ultimoContador == null || contador != ultimoContador) {
               //         ultimoContador = contador;
               //         ClienteApi.enviarMedicion(idMedicion, valorMedido);
               //         Log.d(ETIQUETA_LOG, "Enviado a API: id=" + idMedicion + " valor=" + valorMedido + " cont=" + contador);
               //     } else {
               //         Log.d(ETIQUETA_LOG, "Duplicado (mismo contador), no envío.");
               //     }

            //Envío de la medición a través de la lógica fake.
            ClienteApi.enviarMedicion(idMedicion, valorMedido);

        } catch (Exception e) {
            Log.w(ETIQUETA_LOG, "No es un iBeacon válido o offset distinto: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empieza");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);

                BluetoothDevice device = resultado.getDevice();
                String name = (device != null ? device.getName() : null);

                if (name != null && name.equalsIgnoreCase(dispositivoBuscado)) {
                    procesarTramaBeacon(resultado);
                } else {
                    // fallback: procesar igualmente por si el nombre llega en Scan Response
                    procesarTramaBeacon(resultado);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(ETIQUETA_LOG, "Error en escaneo: " + errorCode);
            }
        };

        ScanFilter filtro = new ScanFilter.Builder()
                .setDeviceName(dispositivoBuscado)
                .build();

        List<ScanFilter> filtros = new ArrayList<>();
        filtros.add(filtro);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        if (!tienePermisosBLE()) {
            Log.w(ETIQUETA_LOG, "Sin permisos BLE en esta versión. Solicitando...");
            solicitarPermisosSiHaceFalta();
            return;
        }

        if (this.elEscanner == null) {
            Log.e(ETIQUETA_LOG, "No hay escáner BLE (¿BT desactivado?).");
            return;
        }

        this.elEscanner.startScan(filtros, settings, this.callbackDelEscaneo);
        Log.d(ETIQUETA_LOG, "Escaneando específicamente: " + dispositivoBuscado);
    }

    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {
        if (this.callbackDelEscaneo == null) return;
        if (!tienePermisosBLE()) return;

        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;
        Log.d(ETIQUETA_LOG, "Escaneo detenido.");
    }

    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "Botón buscar dispositivos BTLE pulsado");
        this.buscarTodosLosDispositivosBTLE();
    }

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "Botón buscar nuestro dispositivo BTLE pulsado");
        this.buscarEsteDispositivoBTLE(NOMBRE_BEACON);
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "Botón detener búsqueda dispositivos BTLE pulsado");
        this.detenerBusquedaDispositivosBTLE();
    }

    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            Log.e(ETIQUETA_LOG, "Este dispositivo no tiene Bluetooth.");
            return;
        }

        // Habilitar BT si está apagado (en 12+ requiere BLUETOOTH_CONNECT concedido)
        if (!bta.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    bta.enable();
                } else {
                    Log.w(ETIQUETA_LOG, "BT apagado y sin BLUETOOTH_CONNECT para encenderlo.");
                }
            } else {
                bta.enable();
            }
        }

        this.elEscanner = bta.getBluetoothLeScanner();
        if (this.elEscanner == null) {
            Log.e(ETIQUETA_LOG, "Error: no se pudo obtener escáner BLE.");
        }
    }

    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, "onCreate(): empieza");
        solicitarPermisosSiHaceFalta();
        inicializarBlueTooth();
        Log.d(ETIQUETA_LOG, "onCreate(): termina");
    }

    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PETICION_PERMISOS) {
            Log.d(ETIQUETA_LOG, "onRequestPermissionsResult()");
            // No hacemos nada más: el usuario puede pulsar nuevamente el botón de escanear
        }
    }
}
