import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-algoritmos',
  templateUrl: './algoritmos.component.html',
  styleUrl: './algoritmos.component.scss'
})
export class AlgoritmosComponent {

  constructor(private router: Router) {
  }

  ngOnInit() {
    this.updateProgressBar();
  }

  // Progress bar

  currentStep = 0;
  totalSteps = 2;
  progress = 0;

  nextStep(): void {
    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
      this.updateProgressBar();
    } else {
      this.onSubmit();
    }
  }

  prevStep(): void {
    if (this.currentStep > 0) {
      this.currentStep--;
      this.updateProgressBar();
    }
    else {
      this.router.navigate(['home']);
    }
  }

  updateProgressBar(): void {
    this.progress = (this.currentStep / this.totalSteps) * 100;
  }

  // Avance

  seleccionado: string;
  items: string[];
  selectedItems: Set<number> = new Set<number>();
  selectedElements: string[];

  tests: string[];
  selectedTestId: number;
  selectedTest: string;
  otro: string = ""

  progressButton(val) {
    this.seleccionado = val
    if (this.seleccionado == 'algoritmos') {
      this.items = ["Kyber1024", "Kyber768", "Kyber512", "BIKE", "Frodo", "Saber", "NTRU", "RSA"];
      this.tests = ["5 llaves", "10 llaves", "20 llaves", "50 llaves", 'Otro'];
    }
    else {
      this.items = [];
      this.tests = [];
    }
    this.currentStep++;
    this.updateProgressBar();
  }

  // algoritmos

  isSelected(index: number): boolean {
    return this.selectedItems.has(index);
  }

  toggleSelection(index: number): void {
    if (this.selectedItems.has(index)) {
      this.selectedItems.delete(index);
    } else {
      this.selectedItems.add(index);
    }
  }

  // tests

  selectItem(index: number): void {
    this.selectedTestId = index;
    this.selectedTest = this.tests[index];
    if (this.selectedTest !== 'Otro') {
      this.otro = '';
    }
  }

  // Results

  results: string[][] = [];

  chartOptions = {}
  chartOptions2 = {}

  terminado = false;
  enProceso = false;

  estado = 0;

  mejorTiempo: number = 0;
  mejorTiempoLabel = "";
  mejorEspacio: number = 0;
  mejorEspacioLabel = "";
  menorTamanioLlavePrivada = "";
  mayorTamanioLlavePrivada = "";
  menorTamanioLlavePublica = "";
  mayorTamanioLlavePublica = "";
  mayorTamanioCiphertext = "";
  mejorTiempoGeneracion = "";
  mejorTiempoCifrado = "";
  mejorTiempoDescifrado = "";

  async onSubmit(): Promise<void> {
    this.selectedElements = Array.from(this.selectedItems).map(
      (index) => this.items[index]
    );

    this.enProceso = true;
    var llaves: string;
    if (this.selectedTest == 'Otro') {
      llaves = this.otro;
    }
    else {
      llaves = this.selectedTest.split(" ")[0];
    }

    for (const selected of this.selectedElements) {
      const [time, memory] = await this.onAsym(llaves, selected);
      this.estado++;
      this.results.push([time, memory, selected]);
    }

    var dataPoints1 = []
    var dataPoints2 = []

    var avance = 1

    this.results.forEach(innerList => {
      dataPoints1.push({ label: innerList[2], x: avance, y: parseInt(innerList[0]) })
      dataPoints2.push({ label: innerList[2], x: avance, y: parseInt(innerList[1]) })

      if(this.mejorTiempoLabel == "" || this.mejorTiempo > parseInt(innerList[0])){
        this.mejorTiempoLabel = innerList[2];
        this.mejorTiempo = parseInt(innerList[0]);
      }

      console.log("new test: " + innerList[2] + " data: " + innerList[1])
      if(this.mejorEspacioLabel == "" || this.mejorEspacio > parseInt(innerList[1])){
        this.mejorEspacioLabel = innerList[2];
        this.mejorEspacio = parseInt(innerList[1]);
        console.log("gana")
      }

      avance++;
    });
  
    type Algorithm = {
      name: string;
      publicKeySize: number;
      privateKeySize: number;
      ciphertextSize: number;
      tiempoGeneracion: string;
      tiempoCifrado: string;
      tiempoDescifrado: string;
    };
    
    const algorithms: Algorithm[] = [
      {
        name: 'Kyber512',
        publicKeySize: 800,
        privateKeySize: 1632,
        ciphertextSize: 768,
        tiempoGeneracion: "~0.02",
        tiempoCifrado: "~0.02",
        tiempoDescifrado: "~0.02"
      },
      {
        name: 'Kyber768',
        publicKeySize: 1184,
        privateKeySize: 2400,
        ciphertextSize: 1088,
        tiempoGeneracion: "~0.03",
        tiempoCifrado: "~0.03",
        tiempoDescifrado: "~0.03"
      },
      {
        name: 'Kyber1024',
        publicKeySize: 1568,
        privateKeySize: 3168,
        ciphertextSize: 1568,
        tiempoGeneracion: "~0.05",
        tiempoCifrado: "~0.05",
        tiempoDescifrado: "~0.05"
      },
      {
        name: 'BIKE',
        publicKeySize: 3107,
        privateKeySize: 6228,
        ciphertextSize: 3107,
        tiempoGeneracion: "~0.15",
        tiempoCifrado: "~0.20",
        tiempoDescifrado: "~0.20"
      },
      {
        name: 'RSA',
        publicKeySize: 3107,
        privateKeySize: 6228,
        ciphertextSize: 3107,
        tiempoGeneracion: "~0.15",
        tiempoCifrado: "~0.20",
        tiempoDescifrado: "~0.20"
      },
      {
        name: 'Frodo',
        publicKeySize: 9616,
        privateKeySize: 19888,
        ciphertextSize: 15744,
        tiempoGeneracion: "~0.80",
        tiempoCifrado: "~0.90",
        tiempoDescifrado: "~1.10"
      },
      {
        name: 'Saber',
        publicKeySize: 1312,
        privateKeySize: 3040,
        ciphertextSize: 1472,
        tiempoGeneracion: "~0.10",
        tiempoCifrado: "~0.15",
        tiempoDescifrado: "~0.15"
      },
      {
        name: 'NTRU',
        publicKeySize: 699,
        privateKeySize: 935,
        ciphertextSize: 699,
        tiempoGeneracion: "~0.05",
        tiempoCifrado: "~0.07",
        tiempoDescifrado: "~0.08"
      }
    ];
    
    function findExtremes(selectedElements) {

      const selectedAlgorithms = algorithms.filter(algo =>
        selectedElements.includes(algo.name)
      );
    
      const mayorTamanioLlavePublica = selectedAlgorithms.reduce((max, algo) =>
        algo.publicKeySize > max.publicKeySize ? algo : max
      ).name;
    
      const menorTamanioLlavePublica = selectedAlgorithms.reduce((min, algo) =>
        algo.publicKeySize < min.publicKeySize ? algo : min
      ).name;

      const mayorTamanioLlavePrivada = selectedAlgorithms.reduce((max, algo) =>
        algo.privateKeySize > max.privateKeySize ? algo : max
      ).name;
    
      const menorTamanioLlavePrivada = selectedAlgorithms.reduce((min, algo) =>
        algo.privateKeySize < min.privateKeySize ? algo : min
      ).name;
    
      const mayorTamanioCiphertext = selectedAlgorithms.reduce((max, algo) =>
        algo.ciphertextSize > max.ciphertextSize ? algo : max
      ).name;
    
      const mejorTiempoGeneracion = selectedAlgorithms.reduce((min, algo) =>
        parseFloat(algo.tiempoGeneracion.replace("~", "")) <
        parseFloat(min.tiempoGeneracion.replace("~", ""))
          ? algo
          : min
      ).name;

      const mejorTiempoCifrado = selectedAlgorithms.reduce((min, algo) =>
        parseFloat(algo.tiempoCifrado.replace("~", "")) <
        parseFloat(min.tiempoCifrado.replace("~", ""))
          ? algo
          : min
      ).name;

      const mejorTiempoDescifrado = selectedAlgorithms.reduce((min, algo) =>
        parseFloat(algo.tiempoDescifrado.replace("~", "")) <
        parseFloat(min.tiempoDescifrado.replace("~", ""))
          ? algo
          : min
      ).name;

      return [mayorTamanioLlavePublica, menorTamanioLlavePublica, mayorTamanioLlavePrivada, menorTamanioLlavePrivada, mayorTamanioCiphertext, mejorTiempoGeneracion, mejorTiempoCifrado, mejorTiempoDescifrado]
    }

    var resultados = findExtremes(this.selectedElements);
    this.mayorTamanioLlavePublica = resultados[0];
    this.menorTamanioLlavePublica = resultados[1];
    this.mayorTamanioLlavePrivada = resultados[2];
    this.menorTamanioLlavePrivada = resultados[3];
    this.mayorTamanioCiphertext = resultados[4];
    this.mejorTiempoGeneracion = resultados[5];
    this.mejorTiempoCifrado = resultados[6];
    this.mejorTiempoDescifrado = resultados[7];

    this.chartOptions = {
      animationEnabled: true,
      title: {
        text: "Gráfico de tiempo de intercambio de llaves simétricas"
      },
      axisY: {
        title: "Tiempo (milisegundos)",
      },
      axisX: {
        title: "Algoritmos",
        interval: 1,
        labelFormatter: function (e) {
          var dataPoint = dataPoints1.find(point => point.x === e.value);
          return dataPoint && dataPoint.x != 0 ? dataPoint.label : "";
        }
      },
      data: [{
        type: "line",
        dataPoints: dataPoints1,
        connectNullData: false
      }]
    };

    this.chartOptions2 = {
      animationEnabled: true,
      title: {
        text: "Gráfico de memoria de intercambio de llaves simétricas"
      },
      axisY: {
        title: "Memoria (KB)",
      },
      axisX: {
        title: "Algoritmos",
        interval: 1,
        labelFormatter: function (e) {
          var dataPoint = dataPoints2.find(point => point.x === e.value);
          return dataPoint && dataPoint.x != 0 ? dataPoint.label : "";
        }
      },
      data: [{
        type: "line",
        dataPoints: dataPoints2,
        connectNullData: false
      }]
    };
    this.enProceso = false;
    this.terminado = true;
  }



  // query

  async onAsym(cantidad, algoritmo): Promise<string[]> {
    return this.getAsymRequest(await this.getAsymBody(cantidad, algoritmo));
  }

  public async getAsymBody(cantidad, algoritmo): Promise<FormData> {
    const formData = new FormData();
    formData.append("cantidad", cantidad);
    formData.append("algorithm", algoritmo);
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
