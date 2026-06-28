/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { motion } from 'motion/react';
import { Sparkles, Award, Clock, Coins, Flame } from 'lucide-react';

// Import the generated image
// @ts-ignore
import heroImage from '../assets/images/academy_hero_1782664664487.jpg';

export default function Hero() {
  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <section className="relative overflow-hidden bg-gradient-to-b from-slate-50 via-white to-slate-50 pt-32 pb-16 text-slate-800 border-b border-slate-200/50">
      {/* Background radial glow */}
      <div className="absolute top-0 left-1/2 -z-10 h-[500px] w-[500px] -translate-x-1/2 rounded-full bg-indigo-500/10 blur-[100px]" />
      <div className="absolute top-40 right-10 -z-10 h-[300px] w-[300px] rounded-full bg-indigo-500/5 blur-[80px]" />

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:items-center">
          
          {/* Text Column */}
          <div className="text-right lg:col-span-7 space-y-6">
            <motion.div 
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              className="inline-flex items-center gap-2 rounded-full border border-indigo-200 bg-indigo-50 px-4 py-2 text-indigo-700 text-sm font-semibold"
            >
              <Sparkles className="h-4 w-4 text-indigo-600" />
              <span>فرصة عمل حقيقية ومربحة من الموبايل وببيتك 🇮🇶</span>
            </motion.div>

            <motion.h1 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.1 }}
              className="text-4xl font-extrabold tracking-tight sm:text-5xl lg:text-6xl text-slate-950 font-sans leading-tight"
            >
              طلّع فلوس وأنت <span className="text-indigo-600">كاعد ببيتك!</span>
            </motion.h1>

            <motion.p 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.2 }}
              className="text-lg text-slate-600 max-w-3xl leading-relaxed font-sans"
            >
              انضم كـ <strong className="text-indigo-600">مروج ميداني / تسويقي</strong> لبرنامج <strong className="text-indigo-600">IQ Ads Academy</strong> - أقوى برنامج عراقي تفاعلي لتعليم إعلانات الإنستغرام وميتا وزيادة المبيعات. واربح عمولة صافية قدرها <strong className="text-indigo-600 text-xl font-black underline decoration-indigo-300">5,000 دينار عراقي</strong> عن كل مشترك يسجل من طرفك!
            </motion.p>

            {/* Quick Stats Grid */}
            <motion.div 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.3 }}
              className="grid grid-cols-2 gap-4 sm:grid-cols-4 pt-4"
            >
              <div className="rounded-2xl border border-slate-200 bg-white p-4 text-center shadow-sm">
                <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600 mb-2">
                  <Coins className="h-5 w-5" />
                </div>
                <div className="text-lg font-bold text-slate-900">5,000 د.ع</div>
                <div className="text-xs text-slate-500">عمولة عن كل بيعة</div>
              </div>

              <div className="rounded-2xl border border-slate-200 bg-white p-4 text-center shadow-sm">
                <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-lg bg-amber-50 text-amber-600 mb-2">
                  <Clock className="h-5 w-5" />
                </div>
                <div className="text-lg font-bold text-slate-900">مرنة 100%</div>
                <div className="text-xs text-slate-500">تحدد ساعات عملك</div>
              </div>

              <div className="rounded-2xl border border-slate-200 bg-white p-4 text-center shadow-sm">
                <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600 mb-2">
                  <Award className="h-5 w-5" />
                </div>
                <div className="text-lg font-bold text-slate-900">حقيبة كاملة</div>
                <div className="text-xs text-slate-500">نصوص ترويج جاهزة</div>
              </div>

              <div className="rounded-2xl border border-slate-200 bg-white p-4 text-center shadow-sm">
                <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-lg bg-amber-50 text-amber-600 mb-2">
                  <Flame className="h-5 w-5" />
                </div>
                <div className="text-lg font-bold text-slate-900">فوري / أسبوعي</div>
                <div className="text-xs text-slate-500">الدفع عبر زين كاش</div>
              </div>
            </motion.div>

            {/* Action Buttons */}
            <motion.div 
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.4 }}
              className="flex flex-col sm:flex-row-reverse gap-4 pt-6"
            >
              <button 
                id="btn-calc"
                onClick={() => scrollToSection('calculator')}
                className="flex items-center justify-center gap-2 rounded-full bg-indigo-600 px-8 py-4 font-bold text-white transition-all hover:bg-indigo-700 shadow-md shadow-indigo-600/10 cursor-pointer"
              >
                <span>احسب أرباحك المتوقعة 📈</span>
              </button>
              
              <button 
                id="btn-toolkit"
                onClick={() => scrollToSection('toolkit')}
                className="flex items-center justify-center gap-2 rounded-full border border-slate-200 bg-white px-8 py-4 font-semibold text-slate-700 transition-all hover:bg-slate-50 cursor-pointer shadow-sm"
              >
                <span>تصفّح حقيبة الترويج والرسائل 🎒</span>
              </button>
            </motion.div>
          </div>

          {/* Visual Column */}
          <motion.div 
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="lg:col-span-5 relative"
          >
            {/* Ambient background glow for image */}
            <div className="absolute -inset-1 rounded-3xl bg-gradient-to-r from-indigo-500 to-indigo-600 opacity-20 blur-lg" />
            
            <div className="relative overflow-hidden rounded-3xl border border-slate-200 bg-white p-2 shadow-md">
              <img 
                src={heroImage} 
                alt="IQ Ads Academy Hero Image" 
                className="w-full rounded-2xl object-cover shadow-sm transition-transform hover:scale-[1.01]"
                referrerPolicy="no-referrer"
              />
              <div className="absolute bottom-4 left-4 right-4 rounded-2xl border border-slate-200 bg-white/95 p-4 text-center shadow-lg backdrop-blur-md">
                <span className="text-xs text-indigo-600 uppercase tracking-wider font-bold block mb-1">البرنامج التفاعلي المساعد</span>
                <p className="text-sm font-sans font-medium text-slate-700">
                  محاكيات وحاسبات متطورة تعطي العميل قيمة حقيقية وسريعة وتسهل عليك إقناعه!
                </p>
              </div>
            </div>
          </motion.div>

        </div>
      </div>
    </section>
  );
}
