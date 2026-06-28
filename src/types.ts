/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface Lead {
  id: string;
  pageName: string;
  category: string;
  status: 'pending' | 'contacted' | 'interested' | 'converted' | 'not_interested';
  notes: string;
  contactDate: string;
}

export interface PitchTemplate {
  id: string;
  title: string;
  category: string;
  description: string;
  script: string;
  angle: string;
}

export interface FAQItem {
  id: string;
  question: string;
  answer: string;
  category: 'payment' | 'tracking' | 'strategy' | 'general';
}
