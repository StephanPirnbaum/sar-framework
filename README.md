# sar-framework

[![CircleCI](https://circleci.com/gh/StephanPirnbaum/sar-framework/tree/master.svg?style=shield&circle-token=75410c38b30f1b9197e8a3ba32d8a4c4a6e9514b)](https://circleci.com/gh/StephanPirnbaum/sar-framework/tree/master) [![GitHub release](https://img.shields.io/badge/Release-v0.1.0-brightgreen.svg)](https://github.com/StephanPirnbaum/sar-framework/releases/latest)


## Usage

A decomposition can be generated using the options:

    -s /path/to/store -c /path/to/configuration.xml

## Configuration

A basic configuration.xml:

```
<Configuration iteration="1" 
               artifact=".*" basePackage=".*" typeName=".*"
               decomposition="hierarchical" optimization="coupling">
</Configuration>
```


