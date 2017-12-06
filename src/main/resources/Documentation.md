Document Delivery API (OCP) will stores workstation, IP and ACF2 information used by OCP API and send PDF documents either to the OCP Client for printing, using registration table to identify client workstation, or to Momentum for mailbox delivery. 


```plantuml
@startuml
actor Client
boundary Document_Delivery_API 
participant OCP 

Client -> Document_Delivery_API  : Post /ftpdeliveries 
Document_Delivery_API  -> OCP: AddFTPDeliveryRq
OCP --> Document_Delivery_API : HTTP status Code
Document_Delivery_API --> Client: HTTP status code

Client -> Document_Delivery_API  : Post /printdeliveries 
Document_Delivery_API  -> OCP: AddPrintDeliveryRq
OCP --> Document_Delivery_API : HTTP status Code
Document_Delivery_API --> Client: HTTP status code

Client -> Document_Delivery_API  : Get /printdeliveries/printers
Document_Delivery_API  -> OCP: retrieve printer list
OCP --> Document_Delivery_API : HTTP status code + RetrievePrintDeliveryPrinterListRs 

Client -> Document_Delivery_API  : Post /printdeliveries/workstationterminal
Document_Delivery_API  -> OCP: AddPrintDeliveryWorkstationTerminalRq
OCP --> Document_Delivery_API : HTTP status code + AddPrintDeliveryWorkstationTerminalRs 


@enduml 
```