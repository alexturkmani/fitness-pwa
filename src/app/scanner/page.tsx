'use client';
import { useState, useEffect, useRef } from 'react';
import { useFoodLog } from '@/hooks/useFoodLog';
import { ScannedProduct, FoodLogEntry } from '@/types';
import { generateId, formatDate, getProteinCalorieRatio, getRatioRating } from '@/lib/utils';
import Card from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import LoadingSpinner from '@/components/ui/LoadingSpinner';
import Toast from '@/components/ui/Toast';
import { ScanLine, Camera, Plus, AlertTriangle, CheckCircle, RefreshCw } from 'lucide-react';

export default function ScannerPage() {
  const { addEntry } = useFoodLog();
  const [scanning, setScanning] = useState(false);
  const [loading, setLoading] = useState(false);
  const [product, setProduct] = useState<ScannedProduct | null>(null);
  const [alternatives, setAlternatives] = useState<any>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const scannerRef = useRef<any>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const startScanner = async () => {
    setError(null);
    setProduct(null);
    setAlternatives(null);
    setScanning(true);

    try {
      const { Html5Qrcode } = await import('html5-qrcode');

      if (scannerRef.current) {
        try { await scannerRef.current.stop(); } catch {}
      }

      const scanner = new Html5Qrcode('scanner-container');
      scannerRef.current = scanner;

      await scanner.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 250, height: 150 } },
        async (decodedText) => {
          try { await scanner.stop(); } catch {}
          setScanning(false);
          await lookupProduct(decodedText);
        },
        () => {}
      );
    } catch (err: any) {
      setError('Camera access denied or not available. Please enable camera permissions.');
      setScanning(false);
    }
  };

  const stopScanner = async () => {
    if (scannerRef.current) {
      try { await scannerRef.current.stop(); } catch {}
    }
    setScanning(false);
  };

  useEffect(() => {
    return () => {
      if (scannerRef.current) {
        try { scannerRef.current.stop(); } catch {}
      }
    };
  }, []);

  const lookupProduct = async (barcode: string) => {
    setLoading(true);
    try {
      const res = await fetch(`/api/nutrition/lookup?barcode=${barcode}`);
      if (!res.ok) {
        setError('Product not found. Try scanning again or enter manually.');
        return;
      }
      const data: ScannedProduct = await res.json();
      setProduct(data);

      // Get AI assessment
      const ratio = getProteinCalorieRatio(data.macros);
      const rating = getRatioRating(ratio);
      if (ratio < 10) {
        try {
          const assessRes = await fetch('/api/ai/assess', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              type: 'food',
              productName: data.name,
              macros: data.macros,
              ratio,
            }),
          });
          if (assessRes.ok) {
            const result = await assessRes.json();
            setAlternatives(result);
          }
        } catch {}
      }
    } catch (err) {
      setError('Failed to look up product. Please check your connection.');
    } finally {
      setLoading(false);
    }
  };

  const addToLog = () => {
    if (!product) return;
    const entry: FoodLogEntry = {
      id: generateId(),
      date: formatDate(new Date()),
      foodName: product.name,
      servingSize: product.servingSize,
      quantity: 1,
      macros: product.macros,
      source: 'scanner',
      barcode: product.barcode,
      createdAt: new Date().toISOString(),
    };
    addEntry(entry);
    setToast(`${product.name} added to food log`);
  };

  const ratio = product ? getProteinCalorieRatio(product.macros) : 0;
  const rating = product ? getRatioRating(ratio) : { label: '', color: '' };

  return (
    <div className="py-6 space-y-6">
      {toast && <Toast message={toast} onClose={() => setToast(null)} />}

      <div>
        <h1 className="text-2xl font-bold text-dark-100">Barcode Scanner</h1>
        <p className="text-dark-400 mt-1">Scan food products for nutritional info</p>
      </div>

      {/* Scanner */}
      {!product && (
        <Card>
          {scanning ? (
            <div className="relative">
              <div id="scanner-container" ref={containerRef} className="rounded-xl overflow-hidden" />
              <div className="scan-line" />
              <Button variant="secondary" size="sm" onClick={stopScanner} className="mt-3 w-full">
                Cancel Scan
              </Button>
            </div>
          ) : (
            <div className="text-center py-8">
              <div className="w-20 h-20 mx-auto mb-4 bg-primary-500/10 rounded-2xl flex items-center justify-center">
                <Camera className="text-primary-400" size={36} />
              </div>
              <p className="text-dark-400 mb-4">Point your camera at a product barcode</p>
              <Button onClick={startScanner} className="w-full">
                <ScanLine size={20} /> Start Scanning
              </Button>
            </div>
          )}
        </Card>
      )}

      {/* Loading */}
      {loading && <LoadingSpinner message="Looking up product..." />}

      {/* Error */}
      {error && (
        <Card className="border-red-500/30 bg-red-500/5">
          <div className="flex items-start gap-3">
            <AlertTriangle className="text-red-400 flex-shrink-0" size={20} />
            <div>
              <p className="text-sm text-dark-300">{error}</p>
              <Button size="sm" variant="secondary" onClick={startScanner} className="mt-2">
                Try Again
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* Product Result */}
      {product && (
        <>
          <Card>
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="font-bold text-lg text-dark-100">{product.name}</h3>
                {product.brand && <p className="text-sm text-dark-500">{product.brand}</p>}
                <p className="text-xs text-dark-500 mt-1">Per {product.servingSize}</p>
              </div>
              {product.imageUrl && (
                <img src={product.imageUrl} alt={product.name} className="w-16 h-16 rounded-xl object-cover" />
              )}
            </div>

            {/* Macros Grid */}
            <div className="grid grid-cols-4 gap-2 mb-4">
              <div className="text-center p-2 bg-dark-800/50 rounded-xl">
                <p className="text-lg font-bold text-dark-100">{product.macros.calories}</p>
                <p className="text-xs text-dark-500">Calories</p>
              </div>
              <div className="text-center p-2 bg-dark-800/50 rounded-xl">
                <p className="text-lg font-bold text-primary-400">{product.macros.protein}g</p>
                <p className="text-xs text-dark-500">Protein</p>
              </div>
              <div className="text-center p-2 bg-dark-800/50 rounded-xl">
                <p className="text-lg font-bold text-accent-400">{product.macros.carbs}g</p>
                <p className="text-xs text-dark-500">Carbs</p>
              </div>
              <div className="text-center p-2 bg-dark-800/50 rounded-xl">
                <p className="text-lg font-bold text-yellow-400">{product.macros.fats}g</p>
                <p className="text-xs text-dark-500">Fats</p>
              </div>
            </div>

            {/* Protein-to-Calorie Ratio */}
            <div className="flex items-center justify-between p-3 bg-dark-800/50 rounded-xl mb-4">
              <span className="text-sm text-dark-400">Protein-to-Calorie Ratio</span>
              <div className="flex items-center gap-2">
                <span className="font-bold" style={{ color: rating.color }}>{ratio.toFixed(1)}g/100cal</span>
                <span className={`badge ${ratio >= 10 ? 'badge-green' : ratio >= 5 ? 'badge-yellow' : 'badge-red'}`}>
                  {rating.label}
                </span>
              </div>
            </div>

            <div className="flex gap-3">
              <Button onClick={addToLog} className="flex-1">
                <Plus size={18} /> Add to Log
              </Button>
              <Button variant="secondary" onClick={() => { setProduct(null); setAlternatives(null); startScanner(); }}>
                <RefreshCw size={18} />
              </Button>
            </div>
          </Card>

          {/* AI Alternatives */}
          {alternatives && (
            <Card className="border-accent-500/30">
              <h3 className="font-semibold text-accent-400 mb-2">AI Assessment</h3>
              <p className="text-sm text-dark-300 mb-3">{alternatives.assessment}</p>
              {alternatives.alternatives && alternatives.alternatives.length > 0 && (
                <div className="space-y-2">
                  <p className="text-xs text-dark-500 font-medium">Better Alternatives:</p>
                  {alternatives.alternatives.map((alt: any, i: number) => (
                    <div key={i} className="flex items-center justify-between p-2.5 bg-dark-800/40 rounded-xl">
                      <div>
                        <p className="text-sm font-medium text-dark-200">{alt.name}</p>
                        <p className="text-xs text-dark-500">{alt.reason}</p>
                      </div>
                      <div className="text-right">
                        <p className="text-xs text-primary-400">{alt.typicalMacros?.protein}g protein</p>
                        <p className="text-xs text-dark-500">{alt.typicalMacros?.calories} cal</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          )}
        </>
      )}
    </div>
  );
}
