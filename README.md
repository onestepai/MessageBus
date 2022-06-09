# MessageBus
Nowadays, more and more system architecture are crossing internet. Several clusters in different regions communicate each other. Such as building a global trading system; building a PaaS communicate with customer services. Do not same as build a micro services architecture in one cluster. Crossing internet architecture is more complicated. There are three major points need to be faced by develop a cross internet system. Heterogeneous communication should be built. It makes developers have to figure out complicated design and implementation. Security issue should be considered. Crossing internet architecture transmit data in internet. A solution of protect transmission and data should be considered. Unstable connection. As communication might cross different regions, even continents, this kind of connections are unstable. 

## Purpose 
To build a unified communication platform, it can meet the needs of intra-LAN and cross-Internet communication at the same time. Improve development efficiency and reduce development difficulty. Ensure the stability and security of communication to the greatest extent.

## Pain points
### The trouble of the physical network environment to the system
When developing a system, it is often necessary to consider various network environments, which increases the difficulty of system design and development. The reason is that the physical network environment is directly exposed in the system, and designers and developers need to design heterogeneous system interfaces for different network communications. Encapsulating the physical environment and exposing only the concept of communication between system services will greatly reduce the difficulty of development.

### unstable network environment
There are great differences in the communication methods and processing difficulties between services within a local area network and across the Internet. For cross-domain Internet communication, it is more complicated, especially for cross-regional communication, which also involves the issue of whether the network is smooth.

### Security issue
At the meantime, the process of crossing the Internet exposes the content of the message to the Internet, and there are many security risks even if SSL communication is used.

## Concept
### TOPIC

Similar to other mainstream messaging frameworks, the concept of TOPIC is used to define the sending of different messages. After MessageBus receives the Topic, it knows how the message is distributed.

### GROUP

When a producer sends a message through TOPIC, the consumer receives and acknowledges the message. The message of the same TOPIC may need to have one consumer or multiple consumers. If a consumer acknowledges a message, other potential consumers will not process the message. GROUP defines multiple groups for the same TOPIC. Each group is guaranteed to receive messages. At the same time, the groups will not affect each other because consumers confirm messages.

### TERMINAL

Each GROUP will add multiple terminals (TERMINAL). Each terminal is an instance of MessageBus in a local area network. Each instance of MessageBus handles messaging both inside and outside the local area network, and communicates with the final consumer.

### CUSTOMER

Messages of MessageBus clients will not be processed and received by other clients TERMINAL. To this end, MessageBus defines the concept of CUSTOMER. Each CUSTOMER can define its own TERMINAL to ensure that its own TOPIC will only go through its own TERMINAL and will not be processed by other customers' TERMINALs.

### ROUTING

ROUTING is to ensure the stability of data communication to the greatest extent in a complex network environment. For each message sent, MessageBus calculates the best message path to ensure that the message is delivered to the consumer. The concept of ROUTING is used to manage and maintain the connectivity status of each TERMINAL. Calculate the best message path based on the connection status between TERMINALs.

## Encapsulation of the network environment
MessageBus encapsulates the physical network environment through the abstract TOPIC and GROUP concepts. Developers do not need to consider the real physical environment when designing the system, but set different TOPICs for different messages and different GROUPs for message targets. The information of specific consumers in the physical network is encapsulated and processed by MesageBus.

## Security mechanism
Client messages are handled independently
First, the TERMINAL between different clients is independent, and the client's message will not be processed by the TERMINAL of other clients.

### Message encryption
#### key pair
Each TOPIC will have a key pair. When sending a message, TERMINAL will obtain the key to encrypt the message before sending the message across the Internet. After the TERMINAL on the consumer side receives the message, it will decrypt it with the public key.

#### Encrypted Message Handling in Messaging

The message path may contain more than the sent TERMINAL and the received TERMINAL, and the path may also contain other transit TERMINALs. These transit TERMINALs are only responsible for delivering messages without decrypting them.

#### How to manage key pairs

The key pair is very important, and if it is accidentally leaked, the message will be stolen. Therefore, a key pair is defined separately for each TOPIC to reduce the impact of key leakage. In addition, the key pair is not stored in TERMINAL, and each time a message is sent, only the encrypted private key is obtained, and a temporary password is obtained at the same time. This temporary password is passed in the message. When decrypting, you need to provide TOPIC, temporary password and TERMINAL's securekey at the same time to obtain the decryption public key. The temporary password will automatically expire in 5 minutes. The securekey of TERMINAL will be obtained and saved in TERMINAL when TERMINAL is registered.

## Special network environment
There are many special cases in the network, for example, some TERMINAL networks do not have a fixed IP to the outside world. In this case, MessageBus divides TERMINAL into two modes: Server and Client. Server is TERMINAL with IP, Client is TERMINAL without IP. Each Client connects to the Server through a TCP long link. Messages sent to Client TERMINAL are forwarded through Server TERMINAL on the path.

![image](https://user-images.githubusercontent.com/107015943/172860479-1a19d712-9247-4e59-9749-4e2ec7d082ee.png)

