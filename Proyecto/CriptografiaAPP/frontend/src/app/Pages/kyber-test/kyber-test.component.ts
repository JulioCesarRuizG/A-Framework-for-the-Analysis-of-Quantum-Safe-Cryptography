import { Component } from '@angular/core';

@Component({
  selector: 'app-kyber-test',
  templateUrl: './kyber-test.component.html',
  styleUrl: './kyber-test.component.scss'
})
export class KyberTestComponent {
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
  constructor() { }

  async ngOnInit() {
    [this.time1, this.memory1] = await this.onAES("1")
    this.isLoading1 = false;
    [this.time2, this.memory2] = await this.onAES("10")
    this.isLoading2 = false;
    [this.time3, this.memory3] = await this.onAES("100")
    this.isLoading3 = false;
    [this.time4, this.memory4] = await this.onAES("1000")
    this.isLoading4 = false;
   // this.time5 = await this.onAES("2000")
    this.isLoading5 = false;

    var dataPoints = [
      { label: "Archivo de 1 MB", x:1, y: parseInt(this.time1) },
      { label: "Archivo de 10 MB", x:10, y: parseInt(this.time2) },
      { label: "Archivo de 100 MB", x:100, y: parseInt(this.time3) },
      { label: "Archivo de 1000 MB", x:1000, y: parseInt(this.time4)  },
      //{ label: "Archivo de 2000 MB",  y: this.time5  }
    ];

    this.chartOptions = {
      animationEnabled: true,
      title: {
        text: "Gráfico de tiempo de cifrado por tamaño de archivo"
      },
      axisX: {
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
      { label: "Archivo de 1 MB", x:1, y: parseInt(this.memory1) },
      { label: "Archivo de 10 MB", x:10, y: parseInt(this.memory2) },
      { label: "Archivo de 100 MB", x:100, y: parseInt(this.memory3) },
      { label: "Archivo de 1000 MB", x:1000, y: parseInt(this.memory4)  },
      //{ label: "Archivo de 2000 MB",  y: this.time5  }
    ];

    this.chartOptions2 = {
      animationEnabled: true,
      title: {
        text: "Gráfico de memoria de cifrado por tamaño de archivo"
      },
      axisX: {
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

  async onAES(size): Promise<string[]> {
    return await this.getKyberRequest(await this.getKyberBody(size));
  }

  public async getKyberBody(size): Promise<FormData> {
    const formData = new FormData();
    formData.append("size", size);
    formData.append("algorithm", "Kyber512");
    return formData;
  }

  public async getKyberRequest(body: FormData): Promise<string[]> {
    let response;
    let res: string[] | undefined;
    try {
      response = await fetch("http://localhost:8080/criptografiaApp/webapi/CryptographyTest/PQCrypto", {
        method: "POST",
        body
      });
      if (response.ok) {
        let elem = await response.text()
        res = elem.split(",");
        
        return [res[0],res[1]];
      }
    } catch (error) {
      throw new Error("Ocurrió un error");
    }
    return res || [];
  }
}
