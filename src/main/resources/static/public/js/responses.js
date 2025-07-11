let example = {
    "cuenta":    {
       "listaCuenta": [      {
          "Banco": "BANCO SANTANDER MEXICO IBM GPO FIN SANTANDER MEXICO",
          "Clabe": "99999999999999999"
       }],
       "numCuenta": "1"
    },
    "cotitulares":    {
       "numCotitular": "0",
       "listaCotitular": []
    },
    "beneficiarios":    {
       "numBeneficiario": "1",
       "listaBeneficiaros": [{"Nombre": "xxxxxx xxxxx xxxxxxx xxxxxx"}]
    },
    "apoderados":    {
       "numApoderado": "0",
       "listaApoderado": []
    },
    "autorizados":    {
       "numAutorizado": "0",
       "listaAutorizado": []
    },
    "respuesta": "Ok",
    "descripcion": "Consulta valida",
    "email": "pruebasqa@bursametrica.com",
    "RFC": "xxxxxxxxxxx",
    "CURP": "xxxxxxxxxxxxxxxxxx            ",
    "direccion": "xxxxxxx xxx xxxx 9999  2 xxx xxxxxxxx xxxxxxxxx METEPEC TOLUCA MEXICO 99999 MEXICO",
    "servicio": "Ejecución",
    "perfil": "PATRIMONIAL",
    "nombre": "pelaez xxxx xxxx",
    "telOfi": "9999999",
    "telDom": "99999999999",
    "telCel": "999999999"
 };
  

 let response2 = {
    "respuesta": null,
    "descripcion": null,
    "movimiento": null,
    "posicion": [
        {
            "EMISORA": "YINN",
            "SERIE": "*",
            "PLAZO": "0",
            "TASA": "0",
            "TITULOS": "2,190",
            "COSTOXTITULOS": "648.280000",
            "COSTOPROMEDIO": "733.898173",
            "VALORMERCADO": "1,419,733.20",
            "PLUS/MINU": "-187,503.80",
            "MERCADO": "1",
            "SUBTOTALDIN": null,
            "SUBTOTALCAP": null,
            "SUBTOTALFON": null
        },
        {
            "EMISORA": "CO129CB",
            "SERIE": "20-6",
            "PLAZO": "0",
            "TASA": "10",
            "TITULOS": "994",
            "COSTOXTITULOS": "1,241.787302",
            "COSTOPROMEDIO": "1,006.086046",
            "VALORMERCADO": "1,234,688.84",
            "PLUS/MINU": "234,639.31",
            "MERCADO": "2",
            "SUBTOTALDIN": null,
            "SUBTOTALCAP": null,
            "SUBTOTALFON": null
        },
        {
            "EMISORA": null,
            "SERIE": null,
            "PLAZO": null,
            "TASA": null,
            "TITULOS": null,
            "COSTOXTITULOS": null,
            "COSTOPROMEDIO": null,
            "VALORMERCADO": null,
            "PLUS/MINU": null,
            "MERCADO": null,
            "SUBTOTALDIN": "1,234,688.84",
            "SUBTOTALCAP": "1,419,733.20",
            "SUBTOTALFON": "0.00"
        }
    ],
    "saldo": [
        {
            "CveDivisa": "MXN",
            "SaldoActual": "30,321.50",
            "SaldoXLiquidar": "8,680.92",
            "SubTotal": "39,002.42"
        },
        {
            "CveDivisa": "Por Asignar MD",
            "SaldoActual": "0.00",
            "SaldoXLiquidar": "0",
            "SubTotal": "0"
        },
        {
            "CveDivisa": "Por Asignar Indeval",
            "SaldoActual": "0.00",
            "SaldoXLiquidar": "0",
            "SubTotal": "0"
        }
    ],
    "total": "2,693,424.46"
} 