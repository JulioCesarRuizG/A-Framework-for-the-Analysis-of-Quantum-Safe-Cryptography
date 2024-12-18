import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-kem-test',
  templateUrl: './kem-test.component.html',
  styleUrl: './kem-test.component.scss'
})
export class KemTestComponent {
  algoritmo: string;

  privateKeySize;
  publicKeySize;
  ciphertextSize;
  tiempoGeneracion;
  tiempoCifrado;
  tiempoDescifrado;

  isLoading1: boolean = true;
  isLoading2: boolean = true;
  isLoading3: boolean = true;
  isLoading4: boolean = true;
  isLoading5: boolean = true;
  time1: string = "";
  time2: string = "";
  time3: string = "";
  time4: string = "";
  time5: string = "";

  memory1: string = "";
  memory2: string = "";
  memory3: string = "";
  memory4: string = "";
  memory5: string = "";

  chartOptions = {}
  chartOptions2 = {}
  constructor(private route: ActivatedRoute) { }

  async ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.algoritmo = params.get('algoritmo');

      switch (this.algoritmo) {
        case 'kyber512':
          this.publicKeySize = 800;
          this.privateKeySize = 1632;
          this.ciphertextSize = 768;
          this.tiempoGeneracion = "~0.02";
          this.tiempoCifrado = "~0.02";
          this.tiempoDescifrado = "~0.02";
          break;
        case 'kyber768':
          this.publicKeySize = 1184;
          this.privateKeySize = 2400;
          this.ciphertextSize = 1088;
          this.tiempoGeneracion = "~0.03";
          this.tiempoCifrado = "~0.03";
          this.tiempoDescifrado = "~0.03";
          break;
        case 'kyber1024':
          this.publicKeySize = 1568;
          this.privateKeySize = 3168;
          this.ciphertextSize = 1568;
          this.tiempoGeneracion = "~0.05";
          this.tiempoCifrado = "~0.05";
          this.tiempoDescifrado = "~0.05";
          break;
        case 'BIKE':
          this.publicKeySize = 3107;
          this.privateKeySize = 6228;
          this.ciphertextSize = 3107;
          this.tiempoGeneracion = "~0.15";
          this.tiempoCifrado = "~0.20";
          this.tiempoDescifrado = "~0.20";
          break;
        case 'RSA':
          this.publicKeySize = 3107;
          this.privateKeySize = 6228;
          this.ciphertextSize = 3107;
          this.tiempoGeneracion = "~0.15";
          this.tiempoCifrado = "~0.20";
          this.tiempoDescifrado = "~0.20";
          break;
        case 'Frodo':
          this.publicKeySize = 9616;
          this.privateKeySize = 19888;
          this.ciphertextSize = 15744;
          this.tiempoGeneracion = "~0.80";
          this.tiempoCifrado = "~0.90";
          this.tiempoDescifrado = "~1.10";
          break;
        case 'Saber':
          this.publicKeySize = 1312;
          this.privateKeySize = 3040;
          this.ciphertextSize = 1472;
          this.tiempoGeneracion = "~0.10";
          this.tiempoCifrado = "~0.15";
          this.tiempoDescifrado = "~0.15";
          break;
        case 'NTRU':
          this.publicKeySize = 699;
          this.privateKeySize = 935;
          this.ciphertextSize = 699;
          this.tiempoGeneracion = "~0.05";
          this.tiempoCifrado = "~0.07";
          this.tiempoDescifrado = "~0.08";
          break;
        default:
          console.error('Algoritmo no reconocido');
      }
    });

    // For cache:
    await this.onAsym("10");

    [this.time1, this.memory1] = await this.onAsym("1")
    this.isLoading1 = false;
    [this.time2, this.memory2] = await this.onAsym("10")
    this.isLoading2 = false;
    [this.time3, this.memory3] = await this.onAsym("50")
    this.isLoading3 = false;
    [this.time4, this.memory4] = await this.onAsym("100")
    this.isLoading4 = false;
    [this.time5, this.memory5] = await this.onAsym("200")
    this.isLoading5 = false;

    var dataPoints = [
      { label: "1 llave", x: 1, y: parseInt(this.time1) },
      { label: "10 llaves", x: 10, y: parseInt(this.time2) },
      { label: "50 llaves", x: 50, y: parseInt(this.time3) },
      { label: "100 llaves", x: 100, y: parseInt(this.time4) },
      { label: "200 llaves", x: 200, y: parseInt(this.time5) }
    ];

    this.chartOptions = {
      animationEnabled: true,
      title: {
        text: "Gráfico de tiempo de intercambio de llaves simétricas"
      },
      axisY: {
        title: "Tiempo (milisegundos)",
      },
      axisX: {
        title: "Llaves",
        interval: 1,
        labelFormatter: function (e) {
          var dataPoint = dataPoints.find(point => point.x === e.value);
          return dataPoint && dataPoint.x != 1 ? dataPoint.label : "";
        }
      },
      data: [{
        type: "line",
        dataPoints: dataPoints,
        connectNullData: false
      }]
    };

    var dataPoints2 = [
      { label: "1 llave", x: 1, y: parseInt(this.memory1) },
      { label: "10 llaves", x: 10, y: parseInt(this.memory2) },
      { label: "50 llaves", x: 50, y: parseInt(this.memory3) },
      { label: "100 llaves", x: 100, y: parseInt(this.memory4) },
      { label: "200 llaves", x: 200, y: parseInt(this.memory5) }
    ];

    this.chartOptions2 = {
      animationEnabled: true,
      title: {
        text: "Gráfico de memoria de intercambio de llaves simétricas"
      },
      axisY: {
        title: "Memoria (KB)",
      },
      axisX: {
        title: "Llaves",
        interval: 1,
        labelFormatter: function (e) {
          var dataPoint = dataPoints2.find(point => point.x === e.value);
          return dataPoint && dataPoint.x != 1 ? dataPoint.label : "";
        }
      },
      data: [{
        type: "line",
        dataPoints: dataPoints2,
        connectNullData: false
      }]
    };
  }

  async onAsym(cantidad): Promise<string[]> {
    return await this.getAsymRequest(await this.getAsymBody(cantidad));
  }

  public async getAsymBody(cantidad): Promise<FormData> {
    const formData = new FormData();
    formData.append("cantidad", cantidad);
    formData.append("algorithm", this.algoritmo);
    return formData;
  }

  public async getAsymRequest(body: FormData): Promise<string[]> {
    let response;
    let res: string[] | undefined;
    try {
      response = await fetch("http://localhost:8080/criptografiaApp/webapi/CryptographyTest/PQCryptoAsim", {
        method: "POST",
        body
      });
      if (response.ok) {
        let elem = await response.text()
        res = elem.split(",");

        return [res[0], res[1]];
      }
    } catch (error) {
      throw new Error("Ocurrió un error");
    }
    return res || [];
  }
}
