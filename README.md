


[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h3 align="center">Smartify</h3>

  <p align="center">
    A web-app that that provides quality of experience to end-users.
    <br />
    <a href="https://github.com/arshiahsn/smartify"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/arshiahsn/smartify">View Demo</a>
    ·
    <a href="https://github.com/arshiahsn/smartify/smartify">Report Bug</a>
    ·
    <a href="https://github.com/arshiahsn/smartify/smartify">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project was a part of my MSc thesis in the Iran University of Science and Technology.
In a smart city, city service providers such as banks, restaurants, landromats, etc., are equiped with instruments that allows them to provide services to the customers in a smart manner. An example of such equipment would be Internet of Things (IoT) sensors that measure the air quality, number of visitors, and humidity in a city service provider. The back-end of this project consumes the API provided by smart city service providers and acquires the quality data. Then, the server calculates a utility associated with each service provider and saves it in the MySQL database. 
The back-end server receives a customer request that includes a source location, a destination location, and a number of requested city services. In other words, a user is located at a location A and intends to go to a location B while visiting a few city services along the way. The server queries the database and calculates a solution that includes a near-optimal order of specific service providers as well as the fastest path to commute the whole journey [1].

* [[1] S. A. Hosseini Bidi, Z. Movahedi and Z. Movahedi, "QoE-Aware Service Composition in Smart Cities Using RESTful IoT," Electrical Engineering (ICEE), Iranian Conference on, 2018, pp. 1559-1564, doi: 10.1109/ICEE.2018.8472501.](https://ieeexplore.ieee.org/abstract/document/8472501?casa_token=LP4LACpc-8EAAAAA:q51dPxONbc4QmfUxwi-kqJMoDJxYr7Q7oQhYoKyL7AZmxXnCqLt3bJzu0cXgLozbu3WDh9WZ)

### Built With

* [Java Spring](https://spring.io)
* [MySQL](https://www.mysql.com)



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/arshiahsn/simple_api.git
   ```
2. Install Maven packages
   ```sh
   ./mvnw clean install
   ```
   
### Running


  ```sh
  ./mvnw run
  ```
  
### Usage
#### Register New Service Provider

  ```sh
  "/registernode",method=POST
  ```
#### Request Service Composition


  ```sh
  "/scheduleservices",method=POST
  ```
#### Get a List of All Registered Service Providers 


  ```sh
  "/getallnodes",method=GET
  ```
#### Unregister a Service Provider 

  ```sh
  "/deletenode/{id}",method=DELETE
  ```
    

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Your Name - [@400_bad_req](https://twitter.com/400_bad_req) - arshiahsn@gmail.com

Project Link: [https://github.com/arshiahsn/simple_api](https://github.com/arshiahsn/simple_api)




<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/arshiahsn/repo.svg?style=for-the-badge
[contributors-url]: https://github.com/arshiahsn/repo/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/arshiahsn/repo.svg?style=for-the-badge
[forks-url]: https://github.com/arshiahsn/repo/network/members
[stars-shield]: https://img.shields.io/github/stars/arshiahsn/repo.svg?style=for-the-badge
[stars-url]: https://github.com/arshiahsn/repo/stargazers
[issues-shield]: https://img.shields.io/github/issues/arshiahsn/repo.svg?style=for-the-badge
[issues-url]: https://github.com/arshiahsn/repo/issues
[license-shield]: https://img.shields.io/github/license/arshiahsn/repo.svg?style=for-the-badge
[license-url]: https://github.com/arshiahsn/simple_api/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/arshiahsn
