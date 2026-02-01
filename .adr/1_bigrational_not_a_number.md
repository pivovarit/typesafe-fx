# ADR-1: Do not extend `Number` in `BigRational` and money primitives

**Date:** 2026-02-01

## Context and Problem Statement

We’re building a money/valuation numeric primitive (`BigRational`) intended for hedging/analytics workloads (positions, offsets, swaps, MTMs, allocations). The goal is to preserve _no silent value drift_ as far as practical: avoid accidental rounding/precision loss and make rounding decisions explicit at well-defined boundaries.

A natural question is whether `BigRational` (and potentially money types built on top of it) should extend `java.lang.Number`, like `BigDecimal` does, to improve interoperability with existing Java APIs.

This is controversial because extending `Number` looks convenient, but it implicitly encourages lossy conversions (`doubleValue()`, `floatValue()`, `intValue()`, etc.) and makes it easy for third-party libraries to silently pull our exact values into floating-point land. In a finance/hedging context, those implicit conversions can create non-obvious discrepancies, reconciliation issues, and hard-to-debug behavior changes (e.g., after refactors, different aggregation orders, or different runtime/library versions).

So the decision is a tradeoff between:
- ecosystem interop
- correctness and safety guarantees due to no implicit lossy conversion paths

## Considered Options

* Option A: `BigRational` does **not** extend `Number`; conversions are explicit
* Option B: `BigRational` extends `Number` and implements `intValue/longValue/floatValue/doubleValue` (plus optional _exact_ variants).

## Decision Outcome and Drivers

Chosen option: A, because:

* Extending `Number` practically requires implementing `doubleValue()`/`floatValue()`, which are inherently lossy for arbitrary rationals and invite accidental precision loss
* Our target domain is sensitive to rounding path-dependence; we want rounding to be explicit and not a side-effect of interop
* We can still achieve most interop needs via adapters

## People
- @pivovarit
