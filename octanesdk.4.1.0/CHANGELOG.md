# Change Log
These are development libraries for Impinj RAIN RFID Readers and Gateways.

## [4.1.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 4.1.0   |
|Octane Java SDK    | 4.1.0   |
|.NET LTK           | 11.2.0  |
|Java LTK           | 11.2.0  |
|C++ LTK for Win32  | 11.2.0  |
|C++ LTK for Linux  | 11.2.0  |
|C LTK for Linux    | 11.2.0  |
|LLRP Definitions   | 1.50.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  8.1.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  8.1.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  8.1.0  |
|Impinj Firmware Upgrade Reference Manual               |  8.1.0  |
|Impinj RShell Reference Manual                         |  8.1.0  |
|Impinj Octane SNMP                                     |  8.1.0  |
|Impinj Octane LLRP                                     |  8.1.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  8.1.0  |
|Impinj Embedded Developers Guide                       |  8.1.0  |
### New Features
- Added netstandard2.0 for backwards compatibility. [PI-28105]
- Added support for start of antenna and end of cycle events. [PI-28322]
- Added support for XPC Words reporting. [PI-24728]
- Added support for R700v2. [PI-29125]
- Deprecated TagFilterVerificationMode and added TagFilterVerificationModeEnum to allow Passive mode to be explicitly set. [PI-29335]
- Added support for R705v2. [PI-29502]

## [4.0.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 4.0.0   |
|Octane Java SDK    | 4.0.0   |
|.NET LTK           | 11.0.0  |
|Java LTK           | 11.0.0  |
|C++ LTK for Win32  | 11.0.0  |
|C++ LTK for Linux  | 11.0.0  |
|C LTK for Linux    | 11.0.0  |
|LLRP Definitions   | 1.48.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  8.0.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  8.0.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  8.0.0  |
|Impinj Firmware Upgrade Reference Manual               |  8.0.0  |
|Impinj RShell Reference Manual                         |  8.0.0  |
|Impinj Octane SNMP                                     |  8.0.0  |
|Impinj Octane LLRP                                     |  8.0.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  8.0.0  |
|Impinj Embedded Developers Guide                       |  8.0.0  |
### New Features
- Upgraded to .NET 5 Framework. [PI-23405]
- Added TLS1.3 support. [PI-23404]
- Added R515 model to the reader enum. [PI-24937]
- Added additional mapping to Beam List and Optimized Antenna List in AntennaUtilities, etc. [PI-24949]
- Added R510 model to the reader enum. [PI-25976]
- Added Monza M730 and M750 to TagModelName. [PI-26202]
- Updated SSH.NET library for compatibility with Octane 8.0.

## [3.7.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.7.0   |
|Octane Java SDK    | 3.7.0   |
|.NET LTK           | 10.46.0 |
|Java LTK           | 10.46.0 |
|C++ LTK for Win32  | 10.46.0 |
|C++ LTK for Linux  | 10.46.0 |
|C LTK for Linux    | 10.46.0 |
|LLRP Definitions   | 1.46.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.6.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.6.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.6.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.6.0  |
|Impinj RShell Reference Manual                         |  7.6.0  |
|Impinj Octane SNMP                                     |  7.6.0  |
|Impinj Octane LLRP                                     |  7.6.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.6.0  |
|Impinj Embedded Developers Guide                       |  7.6.0  |
### New Features
- Updated LLRP package for fix to handle exception on LLRPClient.Open in case of invalid port specified
- The Settings object returned by QuerySettings now includes the fixed transmit frequencies. [PI-13036]
- Resolved an issue with setting MaxTxPower for an antenna. [PI-9381]
- Added support for Tag Filter Verification. [PI-24050]
- Removed constraint of even hexa characters in the filter mask. [PI-24044]
- Added additional mapping to Beam List and Optimized Antenna List in AntennaUtilities. [PI-23139]

## [3.6.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.6.0   |
|Octane Java SDK    | 3.6.0   |
|.NET LTK           | 10.44.0 |
|Java LTK           | 10.44.0 |
|C++ LTK for Win32  | 10.44.0 |
|C++ LTK for Linux  | 10.44.0 |
|C LTK for Linux    | 10.44.0 |
|LLRP Definitions   | 1.44.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.5.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.5.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.5.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.5.0  |
|Impinj RShell Reference Manual                         |  7.5.0  |
|Impinj Octane SNMP                                     |  7.5.0  |
|Impinj Octane LLRP                                     |  7.5.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.5.0  |
|Impinj Embedded Developers Guide                       |  7.5.0  |
### New Features
- Added support for new Morocco region
- Allow for debounce value of zero
- Added ability to set memory block for TagBlockPermalockOp
- Fixed issue with OpSpecId not getting set

## [3.5.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.5.0   |
|Octane Java SDK    | 3.5.0   |
|.NET LTK           | 10.42.0 |
|Java LTK           | 10.42.0 |
|C++ LTK for Win32  | 10.42.0 |
|C++ LTK for Linux  | 10.42.0 |
|C LTK for Linux    | 10.42.0 |
|LLRP Definitions   | 1.42.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.4.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.4.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.4.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.4.0  |
|Impinj RShell Reference Manual                         |  7.4.0  |
|Impinj Octane SNMP                                     |  7.4.0  |
|Impinj Octane LLRP                                     |  7.4.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.4.0  |
|Impinj Embedded Developers Guide                       |  7.4.0  |
### New Features
- Add support for TagImpinjAuthenticateOp for M77x Impinj tag ICs 

## [3.4.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.4.0   |
|Octane Java SDK    | 3.4.0   |
|.NET LTK           | 10.40.0 |
|Java LTK           | 10.40.0 |
|C++ LTK for Win32  | 10.40.0 |
|C++ LTK for Linux  | 10.40.0 |
|C LTK for Linux    | 10.40.0 |
|LLRP Definitions   | 1.38.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.3.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.3.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.3.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.3.0  |
|Impinj RShell Reference Manual                         |  7.3.0  |
|Impinj Octane SNMP                                     |  7.3.0  |
|Impinj Octane LLRP                                     |  7.3.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.3.0  |
|Impinj Embedded Developers Guide                       |  7.3.0  |
### New Features
- Max Sensitivity can now be set on a per antenna basis

## [3.3.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.3.0   |
|Octane Java SDK    | 3.3.0   |
|.NET LTK           | 10.38.0 |
|Java LTK           | 10.38.0 |
|C++ LTK for Win32  | 10.38.0 |
|C++ LTK for Linux  | 10.38.0 |
|C LTK for Linux    | 10.38.0 |
|LLRP Definitions   | 1.38.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.1.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.1.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.1.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.1.0  |
|Impinj RShell Reference Manual                         |  7.1.0  |
|Impinj Octane SNMP                                     |  7.1.0  |
|Impinj Octane LLRP                                     |  7.1.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.1.0  |
|Impinj Embedded Developers Guide                       |  7.1.0  |
### New Features
- Added support for truncated reply

## [3.2.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.2.0   |
|Octane Java SDK    | 3.2.0   |
|.NET LTK           | 10.36.0 |
|Java LTK           | 10.36.0 |
|C++ LTK for Win32  | 10.36.0 |
|C++ LTK for Linux  | 10.36.0 |
|C LTK for Linux    | 10.36.0 |
|LLRP Definitions   | 1.30.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  7.0.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  7.0.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  7.0.0  |
|Impinj Firmware Upgrade Reference Manual               |  7.0.0  |
|Impinj RShell Reference Manual                         |  7.0.0  |
|Impinj Octane SNMP                                     |  7.0.0  |
|Impinj Octane LLRP                                     |  7.0.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  7.0.0  |
|Impinj Embedded Developers Guide                       |  7.0.0  |
### New Features
- Supported reader modes are now retrieved directly from the GET_READER_CAPABILITIES_RESPONSE
- Added support for up to 5 tag filters
- Added support for Impinj R700

## [3.0.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 3.0.0   |
|Octane Java SDK    | 3.0.0   |
|.NET LTK           | 10.34.0 |
|Java LTK           | 10.34.0 |
|C++ LTK for Win32  | 10.34.0 |
|C++ LTK for Linux  | 10.34.0 |
|C LTK for Linux    | 10.34.0 |
|LLRP Definitions   | 1.30.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  6.0.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  6.0.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  6.0.0  |
|Impinj Firmware Upgrade Reference Manual               |  6.0.0  |
|Impinj RShell Reference Manual                         |  6.0.0  |
|Impinj Octane SNMP                                     |  6.0.0  |
|Impinj Octane LLRP                                     |  6.0.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  6.0.0  |
|Impinj Embedded Developers Guide                       |  6.0.0  |
### New Features
- Added support for EU2 readers
- Added support for Speedway readers and gateways using Rev 6.X PCBA

## [2.30.2]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.30.2  |
|Octane Java SDK    | 1.30.0  |
|.NET LTK           | 10.30.1 |
|Java LTK           | 10.30.0 |
|C++ LTK for Win32  | 10.30.0 |
|C++ LTK for Linux  | 10.30.0 |
|C LTK for Linux    | 10.30.0 |
|LLRP Definitions   | 1.28.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.12.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.12.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.12.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.12.0  |
|Impinj RShell Reference Manual                         | 5.12.0  |
|Impinj Octane SNMP                                     | 5.12.0  |
|Impinj Octane LLRP                                     | 5.12.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.12.0  |
|Impinj Embedded Developers Guide                       | 5.12.0  |
### New Features
- Updated SSH.Net version to 2016.1.0 for better performance.
- Samples now accept the reader hostname as a command-line parameter instead of a compile-time constant.

## [2.30.1]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.30.1  |
|Octane Java SDK    | 1.30.0  |
|.NET LTK           | 10.30.1 |
|Java LTK           | 10.30.0 |
|C++ LTK for Win32  | 10.30.0 |
|C++ LTK for Linux  | 10.30.0 |
|C LTK for Linux    | 10.30.0 |
|LLRP Definitions   | 1.28.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.12.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.12.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.12.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.12.0  |
|Impinj RShell Reference Manual                         | 5.12.0  |
|Impinj Octane SNMP                                     | 5.12.0  |
|Impinj Octane LLRP                                     | 5.12.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.12.0  |
|Impinj Embedded Developers Guide                       | 5.12.0  |
### New Features
- TxFrequencies property now available.

## [2.30.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.30.0  |
|Octane Java SDK    | 1.30.0  |
|.NET LTK           | 10.30.0 |
|Java LTK           | 10.30.0 |
|C++ LTK for Win32  | 10.30.0 |
|C++ LTK for Linux  | 10.30.0 |
|C LTK for Linux    | 10.30.0 |
|LLRP Definitions   | 1.28.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.12.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.12.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.12.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.12.0  |
|Impinj RShell Reference Manual                         | 5.12.0  |
|Impinj Octane SNMP                                     | 5.12.0  |
|Impinj Octane LLRP                                     | 5.12.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.12.0  |
|Impinj Embedded Developers Guide                       | 5.12.0  |
### New Features
- .NET Standard 2.0 is now supported in addition to .NET Framework 4.6.1 allowing the LTK to run on many more platforms and frameworks. See https://docs.microsoft.com/en-us/dotnet/standard/net-standard for additional information.
- Added Reader Modes supported by the connected reader are now available from the feature set
- Reduced power frequency list is now available.
- Added support for Monza R6-A tags.

# Changes
- QueryStatus() no longer fails with xSpans
- Zero-length EPCs are now an empty object instead of null
Settings class and associated types now support INotifyPropertyChanged interface and Group types additionally implement INotifyCollectionChanged interface
- Correctly display an error message when applying an invalid configuration to a spacial reader
- Updated some samples


### What's New
- Fixed an issue in the .NET SDK where NULL EPCs would not generate a TagsReported event (PI-4831).

## [2.28.1]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.28.1  |
|Octane Java SDK    | 1.28.0  |
|.NET LTK           | 10.28.0 |
|Java LTK           | 10.28.0 |
|C++ LTK for Win32  | 10.28.0 |
|C++ LTK for Linux  | 10.28.0 |
|C LTK for Linux    | 10.28.0 |
|LLRP Definitions   | 1.28.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.12.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.12.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.12.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.12.0  |
|Impinj RShell Reference Manual                         | 5.12.0  |
|Impinj Octane SNMP                                     | 5.12.0  |
|Impinj Octane LLRP                                     | 5.12.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.12.0  |
|Impinj Embedded Developers Guide                       | 5.12.0  |
### New Features
- Fixed LTK assembly version dependency

## [2.28.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.28.0  |
|Octane Java SDK    | 1.28.0  |
|.NET LTK           | 10.28.0 |
|Java LTK           | 10.28.0 |
|C++ LTK for Win32  | 10.28.0 |
|C++ LTK for Linux  | 10.28.0 |
|C LTK for Linux    | 10.28.0 |
|LLRP Definitions   | 1.28.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.12.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.12.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.12.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.12.0  |
|Impinj RShell Reference Manual                         | 5.12.0  |
|Impinj Octane SNMP                                     | 5.12.0  |
|Impinj Octane LLRP                                     | 5.12.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.12.0  |
|Impinj Embedded Developers Guide                       | 5.12.0  |
### New Features
- Added support for the Speedway R120 Reader
- Added Power Sweep feature
- Added SSH support to RShell in the SDK’s
- Added IPv6 support to RShell in the SDK’s
- Exposed PolarizationControlEnabled field to the Java SDK
- Added Search Mode 6 Dual B to A Select
- Added developer guidance about unavailable data when using direction mode
- .NET LTK and SDK are now available at https://www.nuget.org/
- Bug fixes and performance improvements
### Known Issues
 - Installation of the .NET SDK via the NuGet plugin in Visual Studio 2012 will
   fail with the following message: "'SSH.NET' already has a defined dependency on
   'SshNet.Security.Cryptography'" (See https://github.com/sshnet/SSH.NET/issues/82).
   Workaround: Use Visual Studio 2013+ or manually download and reference the
   assemblies for the OctaneSDK and SSH.NET and SshNet.Securety.Cryptography NuGet
   packages. The .NET LTK does not have a dependency on SSH.NET and thus is not
   affected by this issue.

## [2.26.1]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.26.1  |
|Octane Java SDK    | 1.26.1  |
|.NET LTK           | 10.26.1 |
|Java LTK           | 10.26.1 |
|C++ LTK for Win32  | 10.26.1 |
|C++ LTK for Linux  | 10.26.1 |
|C LTK for Linux    | 10.26.1 |
|LLRP Definitions   | 1.26.1  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.10.1  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.10.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.10.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.10.0  |
|Impinj RShell Reference Manual                         | 5.10.0  |
|Impinj Octane SNMP                                     | 5.10.0  |
|Impinj Octane LLRP                                     | 5.10.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.10.0  |
|Impinj Embedded Developers Guide                       | 5.10.0  |
### New Features
- Bug fixes and performance improvements

## [2.26.0]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.26.0  |
|Octane Java SDK    | 1.26.0  |
|.NET LTK           | 10.26.0 |
|Java LTK           | 10.26.0 |
|C++ LTK for Win32  | 10.26.0 |
|C++ LTK for Linux  | 10.26.0 |
|C LTK for Linux    | 10.26.0 |
|LLRP Definitions   | 1.26.0  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware | 5.10.0  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     | 5.10.0  |
|Impinj xSpan/xArray Installation and Operations Manual | 5.10.0  |
|Impinj Firmware Upgrade Reference Manual               | 5.10.0  |
|Impinj RShell Reference Manual                         | 5.10.0  |
|Impinj Octane SNMP                                     | 5.10.0  |
|Impinj Octane LLRP                                     | 5.10.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           | 5.10.0  |
|Impinj Embedded Developers Guide                       | 5.10.0  |
### New Features
- Added IPv6 support to all libarries
  - Octane .NET SDK
  - Octane Java SDK
  - .NET LTK
  - Java LTK
  - C++ LTK for Win32
  - C++ LTK for Linux
  - C LTK for Linux
- Moved .NET LTK and .NET SDK to .NET Framework version 4.6.1
- Removed xArrayLocationWam SDK example

## [2.24.1]
#### Application Compatibility
| Library           | Version |
|-------------------|---------|
|Octane .NET SDK    | 2.24.1  |
|Octane Java SDK    | 1.24.1  |
|.NET LTK           | 10.24.1 |
|Java LTK           | 10.24.1 |
|C++ LTK for Win32  | 10.24.1 |
|C++ LTK for Linux  | 10.24.1 |
|C LTK for Linux    | 10.24.1 |
|LLRP Definitions   | 1.24.1  |
#### Firmware Compatibility
| Firmware        | Version |
|-----------------|---------|
| Octane Firmware |  5.8.1  |
#### Document Compatibility
| Document                                              | Version |
|-------------------------------------------------------|---------|
|Impinj Speedway Installation and Operations Manual     |  5.8.0  |
|Impinj xSpan/xArray Installation and Operations Manual |  5.8.0  |
|Impinj Firmware Upgrade Reference Manual               |  5.8.0  |
|Impinj RShell Reference Manual                         |  5.8.0  |
|Impinj Octane SNMP                                     |  5.8.0  |
|Impinj Octane LLRP                                     |  5.8.0  |
|Impinj LLRP Tool Kit (LTK) Programmers Guide           |  5.8.0  |
|Impinj Embedded Developers Guide                       |  5.8.0  |
### New Features
- New *SingleTargetReset* search mode.  Used in combination with *SingleTarget*
  inventory to speed the completion of an inventory round by setting tags in B
  state back to A state.
- New *SpatialConfig* class.  Used with xSpan and xArray gateways to configure
  Direction Mode.  Used with the xArray gateway to configure Location Mode.
- New *AntennaUtilities* class.  Used to provide an easier method of selecting
  xSpan and xArray antenna beams by rings and sectors.
- New *ImpinjMarginRead* class.  Used to check if Monza 6 tag IC memory cells
  are fully charged, providing an additional measure of confidence in how well
  the tag has been encoded.
### Changes
- All LTKs and SDKs now support connecting to readers over a secured connection.
  Please see the library-specific documentation for more information on how to
  make your application take advantage of this new feature.
- All LTKs and SDKs now support Octane's new "Direction" feature for xArray.
  Please see the library-specific documentation for more information on how to
  use this new functionality.
- The Java LTK has upgraded the version of Mina it uses to 2.0.9 (up from 1.1.7)
- For xArray-based applications using the SDK, transmit power can now be set
  inside of the LocationConfig object.
- All C and C++ LTKs now rely on the OpenSSL Libraries for network communication.
  For the Win32 LTK, a copy of libeay32.dll and ssleay32.dll are provided.  For
  the Linux C/C++ LTKs, libraries are only provided for the Atmel architecture
  to enable linking for onreader apps.  Libraries for other architectures
  running Linux are not provided as they should already be available from
  your Linux distribution.
- For the C, C++ for Linux, and C++ for Windows libraries, we implemented a fix
  for non-blocking network communication for unencrypted (traditional)
  connections to the reader.  However, if a user is attempting to connect over
  a TLS-encrypted connection, non-blocking calls to recvMessage are still not
  supported
