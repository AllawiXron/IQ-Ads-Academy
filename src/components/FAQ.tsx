/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { HelpCircle, ChevronLeft, Award, HelpCircle as HelpIcon, CheckCircle } from 'lucide-react';
import { FAQ_ITEMS } from '../data';

export default function FAQ() {
  const [openId, setOpenId] = useState<string | null>('faq-1');

  const toggleFAQ = (id: string) => {
    if (openId === id) {
      setOpenId(null);
    } else {
      setOpenId(id);
    }
  };

  return (
    <section id="faq" className="bg-white py-20 text-right border-b border-slate-200/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="mx-auto max-w-3xl text-center mb-16 space-y-4">
          <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-slate-900 font-sans">
            🙋‍♂️ الأسئلة الشائعة للمروج الميداني
          </h2>
          <p className="text-slate-600 text-lg leading-relaxed font-sans">
            كل ما يدور بذهنك من استفسارات حول استلام الأرباح، آلية تتبع مبيعاتك، وكيفية بدء العمل معنا بشكل صحيح ومضمون:
          </p>
          <div className="mx-auto h-1 w-20 rounded-full bg-indigo-600" />
        </div>

        <div className="mx-auto max-w-3xl space-y-4">
          {FAQ_ITEMS.map((faq) => {
            const isOpen = openId === faq.id;
            
            return (
              <div 
                key={faq.id}
                className="rounded-2xl border border-slate-200 bg-slate-50 overflow-hidden shadow-sm hover:border-indigo-350 transition-all"
              >
                
                {/* Accordion Trigger */}
                <button
                  id={`btn-faq-trigger-${faq.id}`}
                  onClick={() => toggleFAQ(faq.id)}
                  className="w-full flex flex-row-reverse items-center justify-between p-6 text-right cursor-pointer"
                >
                  <div className="flex flex-row-reverse items-center gap-3">
                    <div className="rounded-lg bg-indigo-50 border border-indigo-100 p-2 text-indigo-600 shrink-0">
                      <HelpIcon className="h-5 w-5" />
                    </div>
                    <span className="font-bold text-slate-900 text-base font-sans">
                      {faq.question}
                    </span>
                  </div>
                  
                  {/* Rotating Chevron */}
                  <div className={`text-slate-400 transition-transform ${isOpen ? '-rotate-90' : 'rotate-180'}`}>
                    <ChevronLeft className="h-5 w-5" />
                  </div>
                </button>

                {/* Accordion Content with framer motion height/opacity animation */}
                <AnimatePresence initial={false}>
                  {isOpen && (
                    <motion.div
                      initial={{ height: 0, opacity: 0 }}
                      animate={{ height: 'auto', opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }}
                      transition={{ duration: 0.3 }}
                    >
                      <div className="px-6 pb-6 pt-2 border-t border-slate-100 text-sm text-slate-600 leading-relaxed font-sans mr-14 whitespace-pre-line">
                        {faq.answer}
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

              </div>
            );
          })}
        </div>

        {/* Quick Guide and Action Box */}
        <div className="mx-auto max-w-3xl mt-12 rounded-3xl border border-indigo-100 bg-indigo-50/50 p-6 md:p-8 text-center space-y-4">
          <h3 className="text-lg font-bold text-indigo-800 font-sans">جاهز تبلش وتحقق أول عمولة مبيعات الك؟ 🚀</h3>
          <p className="text-xs text-slate-600 leading-relaxed max-w-2xl mx-auto font-sans">
            كل الي تحتاجه هو التسجيل معنا كمروج ميداني معتمد لتوليد كود الخصم ورابط التتبع الخاص بيك. افتح حسابات الإنستغرام والفيسبوك، واستعمل نصوص حقيبة المروج الجاهزة وابدأ مراسلة البيجات والمحلات من اليوم!
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4 pt-2">
            <button
              id="btn-faq-toolkit-scroll"
              onClick={() => {
                const el = document.getElementById('toolkit');
                if (el) el.scrollIntoView({ behavior: 'smooth' });
              }}
              className="rounded-full bg-indigo-600 px-6 py-3 text-xs font-bold text-white hover:bg-indigo-700 transition-colors shadow-sm cursor-pointer"
            >
              عرض نصوص ورسائل الترويج والبدء بالعمل 🚀
            </button>
            <button
              id="btn-faq-calc-scroll"
              onClick={() => {
                const el = document.getElementById('calculator');
                if (el) el.scrollIntoView({ behavior: 'smooth' });
              }}
              className="rounded-full border border-slate-200 bg-white px-6 py-3 text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors cursor-pointer"
            >
              احسب أرباحك المتوقعة 💸
            </button>
          </div>
        </div>

      </div>
    </section>
  );
}
